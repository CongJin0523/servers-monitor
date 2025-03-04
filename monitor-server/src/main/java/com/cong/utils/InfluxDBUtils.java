package com.cong.utils;

import com.cong.entity.DTO.RuntimeData;
import com.cong.entity.VO.request.RuntimeDetailVO;
import com.cong.entity.VO.response.RuntimeHistoryVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class InfluxDBUtils {

  @Value("${spring.influx.token}")
  String token;

  @Value("${spring.influx.url}")
  String url;

  @Value("${spring.influx.bucket}")
  String bucket;

  @Value("${spring.influx.org}")
  String influxOrg;

  @Resource
  ObjectMapper jsonMapper;

  InfluxDBClient client;

  @PostConstruct
  public void init() {
    client = InfluxDBClientFactory.create(url, token.toCharArray());
  }

  public void writeRuntimeData(int clientId, RuntimeDetailVO vo) {
    RuntimeData data = new RuntimeData();
    BeanUtils.copyProperties(vo, data);
    data.setTimestamp(new Date(vo.getTimestamp()).toInstant());
    data.setClientId(clientId);

    WriteApiBlocking writeApi = client.getWriteApiBlocking();
    writeApi.writeMeasurement(this.bucket, this.influxOrg, WritePrecision.NS, data);
  }

  public RuntimeHistoryVO readReamtimeData(int clientId) {
    RuntimeHistoryVO vo = new RuntimeHistoryVO();
    String query = """
                from(bucket: "%s")
                |> range(start: %s)
                |> filter(fn: (r) => r["_measurement"] == "runtime")
                |> filter(fn: (r) => r["clientId"] == "%s")
                """;
    String format = String.format(query, this.bucket, "-1h", clientId);
    // each table save one field
    List<FluxTable> tables = client.getQueryApi().query(format, this.influxOrg);
    int size = tables.size();
    if (size == 0) return vo;
    List<FluxRecord> records = tables.get(0).getRecords();
    for (int i = 0; i < records.size(); i++) {
      JSONObject object = new JSONObject();
      object.put("timestamp", records.get(i).getTime());
      for (int j = 0; j < size; j++) {
        FluxRecord record = tables.get(j).getRecords().get(i);
        object.put(record.getField(), record.getValue());
      }
      try {
        RuntimeData data = jsonMapper.readValue(object.toString(), RuntimeData.class);
        vo.getList().add(data);
      } catch (JsonProcessingException e) {
        log.error("JsonProcessingException {}", e.getMessage());
      }

    }
    return vo;
  }
}
