package com.cong.utils;

import com.cong.entity.ConnectionConfig;
import com.cong.entity.Response;
import com.cong.entity.SystemBaseDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class NetUtils {

  @Lazy
  @Resource
  private ConnectionConfig connectionConfig;

  @Resource
  private ObjectMapper jsonMapper;

  private final HttpClient client = HttpClient.newHttpClient();

  public boolean registerToServer(String address, String token) {
    log.info("Registering, please waiting...");
    Response response = this.doGet("/register", address, token);
    if (response.success()) {
      log.info("Successfully registered");
    } else {
      log.info("Failed to register: {}", response.msg());
    }
    return response.success();
  }

  private Response doGet(String url) {
    return doGet(url, connectionConfig.getAddress(), connectionConfig.getToken());
  }

  private Response doGet(String url, String address, String token) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(address + "/monitor" + url))
        .header("Authorization", token)
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return jsonMapper.readValue(response.body(), Response.class);
    } catch (Exception e) {
      log.error("Error happening when getting a http requesting {} ",e.getMessage());
      return Response.error(e);
    }
  }


  private Response doPost(String url, Object data) {
    try {
      String jsonData = jsonMapper.writeValueAsString(data);
      HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(jsonData))
        .uri(URI.create(connectionConfig.getAddress() + "/monitor" + url))
        .header("Authorization", connectionConfig.getToken())
        .header("Content-Type", "application/json")
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return jsonMapper.readValue(response.body(), Response.class);
    } catch (Exception e) {
      log.error("Error happening when posting a http requesting {} ",e.getMessage());
      return Response.error(e);
    }
  }

  public void updateBaseDetail(SystemBaseDetail baseDetail) {
    Response response = this.doPost("/detail", baseDetail);
    if (response.success()) {
      log.info("Successfully updateBaseDetail");
    } else {
      log.info("Failed to updateBaseDetail: {}", response.msg());
    }
  }
}
