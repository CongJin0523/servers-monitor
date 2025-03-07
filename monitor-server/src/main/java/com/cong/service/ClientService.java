package com.cong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.entity.DTO.Client;
import com.cong.entity.VO.request.ClientDetailVO;
import com.cong.entity.VO.request.RenameClientVO;
import com.cong.entity.VO.request.RenameNodeVO;
import com.cong.entity.VO.request.RuntimeDetailVO;
import com.cong.entity.VO.response.ClientSimpleVO;
import com.cong.entity.VO.response.RuntimeHistoryVO;
import com.cong.entity.VO.response.ClientDetailsVO;
import com.cong.entity.VO.response.ClientPreviewVO;
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
}
