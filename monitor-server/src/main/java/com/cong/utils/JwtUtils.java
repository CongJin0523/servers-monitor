package com.cong.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtUtils {

  @Value("${spring.security.jwt.key}")
  private String key;

  @Value("${spring.security.jwt.expires}")
  private int expires;

  @Resource
  StringRedisTemplate stringRedisTemplate;

  public boolean invalidateJWT (String headerToken) {
    DecodedJWT decodedJWT = this.parseJWT(headerToken);
    if (decodedJWT != null) {
      Date expiresAt = decodedJWT.getExpiresAt();
      String id = decodedJWT.getId();
      return deleteJWT(id, expiresAt);
    }
    return false;
  }

  public boolean deleteJWT (String uuid, Date date) {
    if (this.isValidJWT(uuid)) {
      Date now = new Date();
      long ttl = Math.max(date.getTime() - now.getTime(), 0);
      stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", ttl, TimeUnit.MILLISECONDS);
      return true;
    }
    return false;
  }

  public boolean isValidJWT (String uuid) {
    return !stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid);
  }

  // get the decoded Jwt from the request header
  public DecodedJWT parseJWT(String headerToken) {
    String token = convertToken(headerToken);
    if (token == null) { return null;}
    Algorithm algorithm = Algorithm.HMAC256(key);
    JWTVerifier verifier = JWT.require(algorithm).build();
    try {
      DecodedJWT jwt = verifier.verify(token);
      if (!this.isValidJWT(jwt.getId())) return null;
      Date expiresAt = jwt.getExpiresAt();
      return new Date().after(expiresAt) ? null : jwt;
    } catch (JWTVerificationException e) {
      return null;
    }
  }

  // get the user details from decoded jwt
  public UserDetails toUser(DecodedJWT jwt) {
    Map<String, Claim> claims = jwt.getClaims();
    return User
        .withUsername(claims.get("name").asString())
        .password("*****")
        .authorities(claims.get("authorities").asArray(String.class))
        .build();
  }

  public int toId(DecodedJWT jwt) {
    return jwt.getClaim("id").asInt();
  }

  public String createJwt(UserDetails userDetails, int id, String username) {
    Algorithm algorithm = Algorithm.HMAC256(key);
    Date expire = this.expireTime();
    return JWT.create()
        .withJWTId(UUID.randomUUID().toString())
        .withClaim("id", id)
        .withClaim("name", username)
        .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
        .withExpiresAt(expire)
        .withIssuedAt(new Date())
        .sign(algorithm);
  }

  private Date expireTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, expires);
    return calendar.getTime();
  }

  private String convertToken(String token) {
    if (token == null || !token.startsWith("Bearer ")) {
      return null;
    }
    return token.substring(7);
  }
}
