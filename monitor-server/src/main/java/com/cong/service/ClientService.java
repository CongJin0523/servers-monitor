package com.cong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.entity.DTO.Client;
import com.cong.entity.VO.request.*;
import com.cong.entity.VO.response.*;
import jakarta.validation.Valid;

import java.util.List;

public interface ClientService extends IService<Client> {
  String getRegisterToken();
  boolean verifyAndRegister(String token);

  Client FindClientById(int id);

  Client findClientByToken(String token);
  void updateClientDetail(ClientDetailVO vo, Client client);

  void updateRuntimeDetail(@Valid RuntimeDetailVO vo, Client client);

  List<ClientPreviewVO> getClientList();

  void renameClient(@Valid RenameClientVO vo);

  ClientDetailsVO clientDetails(int clientId);

  RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId);
  RuntimeDetailVO clientRuntimeDetailsNow(int clientId);

  void deleteClient(int clientId);

  void updateNode(@Valid RenameNodeVO vo);

  List<ClientSimpleVO> getSimpleList();

  void saveClientSshConnection(@Valid SshConnectionVO vo);

  SshSettingsVO sshSettings(int clientId);
}
