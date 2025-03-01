package com.cong.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.entity.DTO.Client;
import com.cong.mapper.ClientMapper;
import com.cong.service.ClientService;
import com.cong.utils.SecureRandom;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ClientServiceImpl extends ServiceImpl<ClientMapper ,Client> implements ClientService {

  private String registerToken = SecureRandom.generateRandomString(24);
  /**
   * to save all client info
    */
  private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();
  private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();

  /**
   * init
   * add all client to cache
   */
  @PostConstruct
  public void init() {
    this.list().forEach(this::addClientCache);
  }

  /**
   * check the token and register new client
   * @param token token get from the http header
   * @return if register success
   */
  @Override
  public boolean verifyAndRegister(String token) {
    if (this.registerToken.equals(token)) {
      int id = SecureRandom.generateRandomInt(10000000,90000000);
      Client client = new Client(id, "unnamed", token, new Date());
      if (this.save(client)) {
        log.info("register new client success {}", client);
        this.registerToken = SecureRandom.generateRandomString(24);
        this.addClientCache(client);
        return true;
      }
    }
    return false;
  }

  /**
   * get register Token
   * @return token use to verify
   */
  @Override
  public String getRegisterToken() {
    return registerToken;
  }

  @Override
  public Client FindClientById(int id) {
    return this.clientIdCache.get(id);
  }

  @Override
  public Client findClientByToken(String token) {
    return this.clientTokenCache.get(token);
  }

  /**
   * add new client to cache
   * @param client
   */
  private void addClientCache(Client client) {
    clientIdCache.put(client.getId(), client);
    clientTokenCache.put(client.getToken(), client);
  }

}
