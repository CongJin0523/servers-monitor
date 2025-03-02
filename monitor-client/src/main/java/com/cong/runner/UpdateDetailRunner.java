package com.cong.runner;

import com.cong.entity.SystemBaseDetail;
import com.cong.utils.MonitorUtils;
import com.cong.utils.NetUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateDetailRunner implements ApplicationRunner {
  @Resource
  private NetUtils netTool;

  @Resource
  private MonitorUtils monitorUtils;

  @Override
  public void run(ApplicationArguments args) {
    log.info("Updating detail to the server");
    netTool.updateBaseDetail(monitorUtils.getSystemBaseDetail());

  }
}
