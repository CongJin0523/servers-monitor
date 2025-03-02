package com.cong.configuration;

import com.cong.entity.ConnectionConfig;
import com.cong.utils.MonitorUtils;
import com.cong.utils.NetUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Configuration
@Slf4j
public class ServerConfiguration {
  @Resource
  private NetUtils netTool;

  @Resource
  private MonitorUtils monitorUtils;

  @Resource
  private ObjectMapper jsonMapper;

  @Bean
  ConnectionConfig connectionConfig() {
    log.info("Loading server connection config...");
    ConnectionConfig config = this.getConnectionConfigFromFile();
    if (config == null) {
      config = this.registerToServer();
    }
    System.out.println(monitorUtils.getSystemBaseDetail());
    return config;
  }

  private ConnectionConfig registerToServer() {
    Scanner scanner = new Scanner(System.in);
    String token, address;
    do {
      log.info("Please enter server address, for example 'http://192.168.0.2:8080");
      address = scanner.nextLine();
      log.info("Please enter the token given by server");
      token = scanner.nextLine();
    } while (!netTool.registerToServer(address, token));

    ConnectionConfig config = new ConnectionConfig(address, token);
    this.saveConnectionConfigToFile(config);
    return config;
  }

  private void saveConnectionConfigToFile(ConnectionConfig config) {
    File dir = new File("conf");
    if (!dir.exists() && dir.mkdir()) {
      log.info("Create directory");
      File file = new File("conf/server.json");
      try {
        jsonMapper.writeValue(file, config);
      } catch (Exception e) {
        log.error("Save config file failure with err {}",e.getMessage());
      }
      log.info("Save config file successfully");
    }
  }
  private ConnectionConfig getConnectionConfigFromFile() {
    log.info("Loading connection config from file...");
    File connectionConfigFile = new File("conf/server.json");
    if (connectionConfigFile.exists()) {
      try  {
        return jsonMapper.readValue(connectionConfigFile, ConnectionConfig.class);
      } catch (IOException e) {
        log.error("Error to loading connection config file, {}", e.getMessage());
      }
    }
    return null;
  }
}
