package com.cong.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SystemBaseDetail {
  String osArch;
  String osName;
  String osVersion;
  int osBit;
  String cpuName;
  int cpuCore;
  double memory;
  double disk;
  String ip;
}
