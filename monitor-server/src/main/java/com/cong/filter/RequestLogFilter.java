package com.cong.filter;

import com.cong.utils.Const;
import com.cong.utils.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class RequestLogFilter extends OncePerRequestFilter {

  @Resource
  SnowflakeIdGenerator generator;

  private final Set<String> ignores = Set.of("/swagger-ui", "/v3/api-docs", "/monitor/runtime",
    "/api/monitor/list", "/api/monitor/runtime-now");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(this.isIgnoreUrl(request.getServletPath())) {
      filterChain.doFilter(request, response);
    } else {
      long startTime = System.currentTimeMillis();
      this.logRequestStart(request);
      ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
      filterChain.doFilter(request, wrapper);
      this.logRequestEnd(wrapper, startTime);
      wrapper.copyBodyToResponse();
    }
  }


  private boolean isIgnoreUrl(String url){
    for (String ignore : ignores) {
      if(url.startsWith(ignore)) return true;
    }
    return false;
  }


  public void logRequestEnd(ContentCachingResponseWrapper wrapper, long startTime){
    long time = System.currentTimeMillis() - startTime;
    int status = wrapper.getStatus();
    String content = status != 200 ?
      status + " error" : new String(wrapper.getContentAsByteArray());
    log.info("Request processing time: {}ms | Response result: {}", time, content);

  }


  public void logRequestStart(HttpServletRequest request){
    long reqId = generator.nextId();
    MDC.put("reqId", String.valueOf(reqId));
    JSONObject object = new JSONObject();
    request.getParameterMap().forEach((k, v) -> object.put(k, v.length > 0 ? v[0] : null));
    Object id = request.getAttribute(Const.ATTR_USER_ID);
    if(id != null) {
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      log.info("Request URL: \"{}\" ({}) | Client IP address: {} │ Auth: {} (UID: {}) | Role: {} | Request param: {}",
        request.getServletPath(), request.getMethod(), request.getRemoteAddr(),
        user.getUsername(), id, user.getAuthorities(), object);
    } else {
      log.info("Request URL: \"{}\" ({}) | Client IP address: {} │ Auth: Unauthenticated | Request param: {}",
        request.getServletPath(), request.getMethod(), request.getRemoteAddr(), object);
    }
  }
}
