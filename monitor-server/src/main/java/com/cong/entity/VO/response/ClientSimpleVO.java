package com.cong.entity.VO.response;

import lombok.Data;

@Data
public class ClientSimpleVO {
  int id;
  String name;
  String location;
  String osName;
  String osVersion;
  String ip;
}
