package com.cong;

import com.cong.entity.DTO.Account;
import com.cong.mapper.AccountMapper;
import com.cong.utils.InfluxDBUtils;
import com.cong.utils.SecureRandom;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MonitorServerApplicationTests {
  @Resource
  private AccountMapper accountMapper;

  @Resource
  private InfluxDBUtils influxDBUtils;
  @Test
  void contextLoads() {
    System.out.println(new BCryptPasswordEncoder().encode("123456"));
  }

  @Test
  void mybatisTest(){
    Account account = accountMapper.selectById(1);
    System.out.println(account);
  }

  @Test
  void ramdamId(){
    int id = SecureRandom.generateRandomInt(10000000,90000000);
    System.out.println(id);
  }

  @Test
  void getInfluxDB(){
    influxDBUtils.readReamtimeData(93437122);
  }

}
