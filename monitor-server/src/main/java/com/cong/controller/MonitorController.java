package com.cong.controller;

import com.cong.entity.DTO.Account;
import com.cong.entity.RestBean;
import com.cong.entity.VO.request.RenameClientVO;
import com.cong.entity.VO.request.RenameNodeVO;
import com.cong.entity.VO.request.RuntimeDetailVO;
import com.cong.entity.VO.request.SshConnectionVO;
import com.cong.entity.VO.response.*;
import com.cong.service.AccountService;
import com.cong.service.ClientService;
import com.cong.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
  @Resource
  ClientService clientService;

  @Resource
  AccountService accountService;

  @GetMapping("/list")
  public RestBean<List<ClientPreviewVO>> clientList(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                                    @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    List<ClientPreviewVO> clientList = clientService.getClientList();
    if (this.isAdminAccount(role)) {
      return RestBean.success(clientList);
    } else {
      List<Integer> ids = this.accountAccessClients(uid);
      return RestBean.success(clientList.stream()
        .filter(vo -> ids.contains(vo.getId()))
        .toList());
    }
  }

  @PostMapping("/rename")
  public RestBean<Void> rename(@RequestBody @Valid RenameClientVO vo,
                               @RequestAttribute(Const.ATTR_USER_ID) int uid,
                               @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.permissionCheck(uid, role, vo.getId())){
      clientService.renameClient(vo);
      return RestBean.success();
    } else {
      return RestBean.noPermission();
    }

  }

  @GetMapping("/details")
  public RestBean<ClientDetailsVO> details(int clientId,
                                           @RequestAttribute(Const.ATTR_USER_ID) int uid,
                                           @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.permissionCheck(uid, role, clientId)){
      return RestBean.success(clientService.clientDetails(clientId));
    } else {
      return RestBean.noPermission();
    }

  }

  @GetMapping("/runtime-history")
  public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(int clientId,
                                                          @RequestAttribute(Const.ATTR_USER_ID) int uid,
                                                          @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.permissionCheck(uid, role, clientId)){
      return RestBean.success(clientService.clientRuntimeDetailsHistory(clientId));
    } else {
      return RestBean.noPermission();
    }


  }

  @GetMapping("/runtime-now")
  public RestBean<RuntimeDetailVO> runtimeNow(int clientId,
                                              @RequestAttribute(Const.ATTR_USER_ID) int uid,
                                              @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.permissionCheck(uid, role, clientId)){
      return RestBean.success(clientService.clientRuntimeDetailsNow(clientId));
    } else {
      return RestBean.noPermission();
    }

  }

  @GetMapping("/register")
  public RestBean<String> registerToken(@RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if (this.isAdminAccount(role)) {
      return RestBean.success(clientService.getRegisterToken());
    } else {
      return RestBean.noPermission();
    }

  }

  @GetMapping("/delete")
  public RestBean<Void> deleteClient(int clientId,
                                     @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if (this.isAdminAccount(role)) {
      clientService.deleteClient(clientId);
      return RestBean.success();
    } else {
      return RestBean.noPermission();
    }

  }

  @PostMapping("/node")
  public RestBean<Void> updateNode(@RequestBody @Valid RenameNodeVO vo,
                                   @RequestAttribute(Const.ATTR_USER_ID) int uid,
                                   @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.permissionCheck(uid, role, vo.getId())){
      clientService.updateNode(vo);
      return RestBean.success();
    } else {
      return RestBean.noPermission();
    }
  }

  @GetMapping("/simple-list")
  public RestBean<List<ClientSimpleVO>> simpleClientList(@RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.isAdminAccount(role)) {
      return RestBean.success(clientService.getSimpleList());
    } else {
      return RestBean.noPermission();
    }

  }

  @PostMapping("/ssh-save")
  public RestBean<Void> saveSshConnection(@RequestBody @Valid SshConnectionVO vo,
                                          @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                          @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
    if(this.permissionCheck(userId, userRole, vo.getId())) {
      clientService.saveClientSshConnection(vo);
      return RestBean.success();
    } else {
      return RestBean.noPermission();
    }
  }

  @GetMapping("/ssh")
  public RestBean<SshSettingsVO> sshSettings(int clientId,
                                             @RequestAttribute(Const.ATTR_USER_ID) int uid,
                                             @RequestAttribute(Const.ATTR_USER_ROLE) String role) {
    if(this.permissionCheck(uid, role, clientId)) {
      return RestBean.success(clientService.sshSettings(clientId));
    } else {
      return RestBean.noPermission();
    }
  }


  private List<Integer> accountAccessClients(int uid) {
    Account account = accountService.getById(uid);
    return account.getClients();
  }

  private boolean isAdminAccount(String role) {
    role = role.substring(5);
    return Const.ROLE_ADMIN.equals(role);
  }

  private boolean permissionCheck(int uid, String role, int clientId) {
    if(this.isAdminAccount(role)) return true;
    return this.accountAccessClients(uid).contains(clientId);
  }
}

