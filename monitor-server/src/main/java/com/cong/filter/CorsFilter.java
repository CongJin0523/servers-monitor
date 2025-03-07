package com.cong.filter;

import com.cong.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
  @Value("${spring.web.cors.origin}")
  String origin;

  @Value("${spring.web.cors.credentials}")
  boolean credentials;

  @Value("${spring.web.cors.methods}")
  String methods;


  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    this.addCorsHeader(request, response);
    chain.doFilter(request, response);
  }

  private void addCorsHeader(HttpServletRequest request,HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin",this.resolveOrigin(request));
    response.setHeader("Access-Control-Allow-Methods", this.resolveMethod());
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  }

  private String resolveMethod(){
    return methods.equals("*") ? "GET, POST, PUT" : methods;
  }

  private String resolveOrigin(HttpServletRequest request){
    return origin.equals("*") ? request.getHeader("Origin") : origin;
  }
}
