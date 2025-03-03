package com.cong.task;

import com.cong.entity.RuntimeDetail;
import com.cong.utils.MonitorUtils;
import com.cong.utils.NetUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonitorJobBean extends QuartzJobBean {

  @Resource
  NetUtils netUtils;

  @Resource
  MonitorUtils monitorUtils;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    log.info("update runtime info");
    RuntimeDetail runtimeDetail = monitorUtils.getRuntimeDetail();
    netUtils.updateRuntimeDetails(runtimeDetail);
  }
}
