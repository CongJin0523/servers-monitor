package com.cong.config;

import com.auth0.jwt.JWT;
import com.cong.context.AccountContext;
import com.cong.entity.DTO.Account;
import com.cong.entity.RestBean;
import com.cong.entity.VO.response.AuthorizeVO;
import com.cong.filter.JwtAuthorizeFilter;
import com.cong.utils.Const;
import com.cong.utils.CopyProperties;
import com.cong.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@Slf4j
public class SecurityConfiguration {

  @Resource
  JwtUtils jwtUtils;

  @Resource
  JwtAuthorizeFilter jwtAuthorizeFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(conf -> conf
            .requestMatchers("/api/auth/**", "/error").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(conf -> conf
            .loginProcessingUrl("/api/auth/login")
            .failureHandler(this::onAuthenticationFailure)
            .successHandler(this::onAuthenticationSuccess)
            .permitAll()
        )
        .logout(conf -> conf
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(this::onLogoutSuccess)
        )
        .exceptionHandling(conf -> conf
            .accessDeniedHandler(this::onAccessDenied)
            .authenticationEntryPoint(this::onUnauthorized)
        )
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(conf -> conf
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException {
    String authorization = req.getHeader("Authorization");

    resp.setContentType("application/json;charset=utf-8");
    if (jwtUtils.invalidateJWT(authorization)) {
      resp.getWriter().write(RestBean.success().toJsonString());
    } else {
      resp.getWriter().write(RestBean.fail(400, "Logout failure!").toJsonString());
    }

  }
  private void onUnauthorized(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException, ServletException {
    resp.setContentType("application/json;charset=utf-8");
    resp.getWriter().write(RestBean.unauthorized(e.getMessage()).toJsonString());
  }

  private void onAccessDenied(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException e) throws IOException, ServletException {
    resp.setContentType("application/json;charset=utf-8");
    resp.getWriter().write(RestBean.fail(403, e.getMessage()).toJsonString());
  }




  private void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException {
    resp.setContentType("application/json;charset=utf-8");
    resp.getWriter().write(RestBean.unauthorized(e.getMessage()).toJsonString());
  }

  private void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException {
    resp.setContentType("application/json;charset=utf-8");
    User user = (User) authentication.getPrincipal();
    Account account = AccountContext.get();
    log.info("user login {}", user);
    String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());
    AuthorizeVO authorizeVO = account.toObject(
        AuthorizeVO.class,
        ov -> {
          ov.setToken(token);
          ov.setExpire(JWT.decode(token).getExpiresAt());
        });
//    CopyProperties.copyProperties(account, authorizeVO);
//    BeanUtils.copyProperties(account, authorizeVO);
    resp.getWriter().write(RestBean.success(authorizeVO).toJsonString());
  }


}
