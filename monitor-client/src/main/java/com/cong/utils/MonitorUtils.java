package com.cong.utils;

import com.cong.entity.SystemBaseDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MonitorUtils {

  private final SystemInfo si = new SystemInfo();
  private final Properties prop = System.getProperties();


  public SystemBaseDetail getSystemBaseDetail() {
    OperatingSystem os = si.getOperatingSystem();
    HardwareAbstractionLayer hal = si.getHardware();

    double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024;
    double memory = hal.getMemory().getTotal() / 1024.0 / 1024 / 1024;
    String ipAddress = String.valueOf(this.getNetworkIF(hal));
    return new SystemBaseDetail()
      .setOsArch(prop.getProperty("os.arch"))
      .setOsName(os.getFamily())
      .setOsVersion(os.getVersionInfo().getVersion())
      .setOsBit(os.getBitness())
      .setCpuName(hal.getProcessor().getProcessorIdentifier().getName())
      .setCpuCore(hal.getProcessor().getLogicalProcessorCount())
      .setMemory(memory)
      .setDisk(diskSize)
      .setIp(ipAddress);
  }

  private String getNetworkIF(HardwareAbstractionLayer hal) {

      List<NetworkIF> NetworkIF = hal.getNetworkIFs().stream()
        .filter(networkIF -> {
          NetworkInterface ni = networkIF.queryNetworkInterface();
          try {
            return ni != null && !ni.isLoopback() && !ni.isPointToPoint() && !ni.isVirtual()
              && (ni.getName().startsWith("eth") || ni.getName().startsWith("en"));
          } catch (SocketException e) {
            log.error("Fail to read the network info, error {}", e.getMessage());
            return false;
          }
        })
        .filter(networkIF -> {
          return networkIF.getIPv4addr() != null && networkIF.getIPv4addr().length > 0;
        })
        .toList();

      if (NetworkIF.isEmpty()) {
        return null;
      } else {
        return NetworkIF.get(0).getIPv4addr()[0];
      }
  }
}
