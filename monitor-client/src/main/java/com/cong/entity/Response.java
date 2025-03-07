package com.cong.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public record Response (int id, int code, Object data , String msg) {

  public boolean success() {
    return code == 200;
  }

  public static  Response error(Exception e) {
    return new Response(0, 500, null, e.getMessage());
  }
  public String toJson() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    objectMapper.setDateFormat(dateFormat);
    return objectMapper.writeValueAsString(this.data);
  }

}
