package com.cong.entity.VO.response;

import lombok.Data;
import org.json.JSONArray;

import java.util.List;

@Data
public class SubAccountVO {
  int id;
  String username;
  String email;
  List<Integer> clients;
}