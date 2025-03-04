package com.cong.entity.VO.response;

import com.cong.entity.DTO.RuntimeData;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

@Data
public class RuntimeHistoryVO {
  double disk;
  double memory;
  List<RuntimeData> list = new LinkedList<>();
}
