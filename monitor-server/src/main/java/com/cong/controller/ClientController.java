package com.cong.controller;

import com.cong.entity.DTO.Client;
import com.cong.entity.RestBean;
import com.cong.entity.VO.request.ClientDetailVO;
import com.cong.service.ClientService;
import com.cong.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitor")
public class ClientController {

  @Resource
  private ClientService clientService;

  @GetMapping("/register")
  public RestBean<Void> registerClient(@RequestHeader("Authorization") String token) {

    return clientService.verifyAndRegister(token)
      ? RestBean.success()
      : RestBean.fail(401, "Client register failure, please check token");
  }

  @PostMapping("/detail")
  public RestBean<Void> clientBaseInfo( @RequestBody ClientDetailVO vo,
                                       @RequestAttribute(Const.ATTR_CLIENT) Client client) {
    clientService.updateClientDetail(vo, client);
    return RestBean.success();
  }
}
