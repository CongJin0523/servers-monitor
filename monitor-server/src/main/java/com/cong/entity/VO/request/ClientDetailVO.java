package com.cong.entity.VO.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;


@Data
public class ClientDetailVO {
  @NotNull
  String osArch;
  @NotNull
  String osName;
  @NotNull
  String osVersion;
  @NotNull
  int osBit;
  @NotNull
  String cpuName;
  @NotNull
  int cpuCore;
  @NotNull
  double memory;
  @NotNull
  double disk;
  @NotNull
  String ip;
}
