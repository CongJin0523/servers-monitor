package com.cong.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cong.entity.DTO.Account;
import com.cong.entity.DTO.Client;
import com.cong.entity.RestBean;
import com.cong.service.AccountService;
import com.cong.service.ClientService;
import com.cong.utils.Const;
import com.cong.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

  @Resource
  JwtUtils jwtUtils;

  @Resource
  ClientService clientService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String authorization = request.getHeader("Authorization");

    // if uri belongs to client, don't need jwt authenticate
    String uri = request.getRequestURI();
    if (uri.startsWith("/monitor"))
    {
      if(!uri.endsWith("/register")) {
        Client client = clientService.findClientByToken(authorization);
        if(client == null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setCharacterEncoding("UTF-8");
          response.getWriter().write(RestBean.unauthorized("unregistered").toJsonString());
          return;
        } else {
          request.setAttribute(Const.ATTR_CLIENT, client);
        }
      }
    } else {
      DecodedJWT jwt = jwtUtils.parseJWT(authorization);
      if (jwt != null) {
        UserDetails user = jwtUtils.toUser(jwt);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Manual authorities, unsafe in mult thread
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.setAttribute(Const.ATTR_USER_ID, jwtUtils.toId(jwt));
        request.setAttribute(Const.ATTR_USER_ROLE, new ArrayList<>(user.getAuthorities()).get(0).getAuthority());

        if(request.getRequestURI().startsWith("/terminal/") && !accessShell(
          (int) request.getAttribute(Const.ATTR_USER_ID),
          (String) request.getAttribute(Const.ATTR_USER_ROLE),
          Integer.parseInt(request.getRequestURI().substring(10)))) {
          response.setStatus(401);
          response.setCharacterEncoding("utf-8");
          response.getWriter().write(RestBean.fail(401, "无权访问").toJsonString());
          return;
        }
    }

    }
    filterChain.doFilter(request, response);
  }

  @Resource
  AccountService accountService;

  private boolean accessShell(int userId, String userRole, int clientId) {
    if(Const.ROLE_ADMIN.equals(userRole.substring(5))) {
      return true;
    } else {
      Account account = accountService.getById(userId);
      return account.getClients().contains(clientId);
    }
  }
}
