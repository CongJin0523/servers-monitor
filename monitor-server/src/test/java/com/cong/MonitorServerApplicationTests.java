package com.cong;

import com.cong.entity.DTO.Account;
import com.cong.mapper.AccountMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MonitorServerApplicationTests {
  @Resource
  private AccountMapper accountMapper;

  @Test
  void contextLoads() {
    System.out.println(new BCryptPasswordEncoder().encode("123456"));
  }

  @Test
  void mybatisTest(){
    Account account = accountMapper.selectById(1);
    System.out.println(account);
  }

}
