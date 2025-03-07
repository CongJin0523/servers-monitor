package com.cong;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.SystemInfo;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

@SpringBootTest
class MonitorClientApplicationTests {

  @Test
  void contextLoads() {
    SystemInfo si = new SystemInfo();
    Properties prop = System.getProperties();
    double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024;
    System.out.println(diskSize);
    System.out.println(prop.getProperty("os.name"));
    System.out.println(si.getHardware().getProcessor());
  }


}
