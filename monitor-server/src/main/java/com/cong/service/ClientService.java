package com.cong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.entity.DTO.Client;

public interface ClientService extends IService<Client> {
  String getRegisterToken();
  boolean verifyAndRegister(String token);

  Client FindClientById(int id);

  Client findClientByToken(String token);
}
