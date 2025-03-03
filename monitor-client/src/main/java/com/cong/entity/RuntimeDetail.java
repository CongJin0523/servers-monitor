package com.cong.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RuntimeDetail {
  long timestamp;
  double cpuUsage;
  double memoryUsage;
  double diskUsage;
  double diskRead;
  double diskWrite;
  double networkUpload;
  double networkDownload;
}
