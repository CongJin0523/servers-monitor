package com.cong.controller;

import com.cong.entity.RestBean;
import com.cong.entity.VO.request.RenameClientVO;
import com.cong.entity.VO.response.ClientPreviewVO;
import com.cong.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
  @Resource
  ClientService clientService;

  @GetMapping("/list")
  public RestBean<List<ClientPreviewVO>> clientList() {
    return RestBean.success(clientService.getClientList());
  }

  @PostMapping("/rename")
  public RestBean<Void> rename(@RequestBody @Valid RenameClientVO vo) {
    clientService.renameClient(vo);
    return RestBean.success();
  }
}
