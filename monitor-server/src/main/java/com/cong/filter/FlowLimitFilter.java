package com.cong.filter;

import com.cong.entity.RestBean;
import com.cong.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(-101)
public class FlowLimitFilter extends HttpFilter {

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    String ip = request.getRemoteAddr();
    if(this.tryCount(ip)){
      chain.doFilter(request, response);
    } else {
      this.writeBlockMessage(response);
    }
  }
  private void writeBlockMessage(HttpServletResponse resp) throws IOException {
    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    resp.setContentType("application/json;charset=utf-8");
    resp.getWriter().write(RestBean.forbidden("Request too frequently, Please try later.").toJsonString());
  }
  private boolean tryCount(String ip){
    synchronized (ip.intern()) {
      if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip))){
        return false;
      }
      return limitFrequencyCheck(ip);
    }

  }

  private boolean limitFrequencyCheck(String ip){
    if (stringRedisTemplate.hasKey(Const.FLOW_LIMIT_COUNTER + ip)) {
      Long count = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
      if (count > 20) {
        stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 60, TimeUnit.MINUTES);
        return false;
      }
    } else {
      stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS);
    }
    return true;
  }
}
