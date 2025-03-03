package com.cong.utils;

import com.cong.entity.DTO.RuntimeData;
import com.cong.entity.VO.request.RuntimeDetailVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Date;

@Component
public class InfluxDBUtils {

  @Value("${spring.influx.token}")
  String token;

  @Value("${spring.influx.url}")
  String url;

  @Value("${spring.influx.bucket}")
  String bucket;

  @Value("${spring.influx.org}")
  String org;


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
    writeApi.writeMeasurement(this.bucket, this.org, WritePrecision.NS, data);
  }
}
