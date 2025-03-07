package com.cong.utils;

import com.cong.entity.ConnectionConfig;
import com.cong.entity.RuntimeDetail;
import com.cong.entity.SystemBaseDetail;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

@Component
@Slf4j
public class MonitorUtils {



  private final SystemInfo si = new SystemInfo();
  private final Properties prop = System.getProperties();

  @Lazy
  @Resource
  ConnectionConfig config;

  public SystemBaseDetail getSystemBaseDetail() {
    OperatingSystem os = si.getOperatingSystem();
    HardwareAbstractionLayer hal = si.getHardware();

    double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024;
    double memory = hal.getMemory().getTotal() / 1024.0 / 1024 / 1024;
    String ipAddress = Objects.requireNonNull(this.getNetworkIF(hal)).getIPv4addr()[0];
    return new SystemBaseDetail()
      .setOsArch(prop.getProperty("os.arch"))
      .setOsName(os.getFamily())
      .setOsVersion(os.getVersionInfo().getVersion())
      .setOsBit(os.getBitness())
      .setCpuName(hal.getProcessor().getProcessorIdentifier().getName())
      .setCpuCore(hal.getProcessor().getPhysicalProcessorCount())
      .setMemory(memory)
      .setDisk(diskSize)
      .setIp(ipAddress);
  }

  public List<String> listNetworkInterfaceName() {
    HardwareAbstractionLayer hardware = si.getHardware();
    return hardware.getNetworkIFs()
      .stream()
      .map(NetworkIF::getName)
      .toList();
  }

  private NetworkIF getNetworkIF(HardwareAbstractionLayer hal) {
    try {
      String target = config.getNetworkInterface();
      List<NetworkIF> ifs = hal.getNetworkIFs()
        .stream()
        .filter(inter -> inter.getName().equals(target))
        .toList();
      if (!ifs.isEmpty()) {
        return ifs.get(0);
      } else {
        throw new IOException("Network adaptor name is incorrect，cannot find it: " + target);
      }
    } catch (IOException e) {
      log.error("Error reading network interface information.", e);
    }
    return null;
  }

  public RuntimeDetail getRuntimeDetail() {
    double statisticTime = 1.0;
    try {
      HardwareAbstractionLayer hal = si.getHardware();
      NetworkIF networkIF = Objects.requireNonNull(this.getNetworkIF(hal));
      CentralProcessor processor = hal.getProcessor();
      double upload = networkIF.getBytesSent();
      double download = networkIF.getBytesRecv();
      double read = hal.getDiskStores().stream()
        .mapToLong(HWDiskStore::getReadBytes)
        .sum();
      double write = hal.getDiskStores().stream()
        .mapToLong(HWDiskStore::getWriteBytes)
        .sum();
      long[] ticks = processor.getSystemCpuLoadTicks();
      Thread.sleep((long) (statisticTime * 1000));
      networkIF = Objects.requireNonNull(this.getNetworkIF(hal));
      upload = (networkIF.getBytesSent() - upload) / statisticTime;
      download = (networkIF.getBytesRecv() - download) / statisticTime;
      read = (hal.getDiskStores().stream()
        .mapToLong(HWDiskStore::getReadBytes)
        .sum()
        - read) / statisticTime;
      write = (hal.getDiskStores().stream()
        .mapToLong(HWDiskStore::getWriteBytes)
        .sum()
        - write) / statisticTime;
      double memory = (hal.getMemory().getTotal() - hal.getMemory().getAvailable()) / 1024.0 / 1024 / 1024;
      double disk = Arrays.stream(File.listRoots()).mapToLong(file -> file.getTotalSpace() - file.getFreeSpace()).sum() / 1024.0 / 1024 / 1024; ;
      return new RuntimeDetail()
        .setCpuUsage(this.calculateCpuUsage(processor, ticks))
        .setMemoryUsage(memory)
        .setDiskUsage(disk)
        .setNetworkUpload(upload / 1024.0)
        .setNetworkDownload(download / 1024.0)
        .setDiskRead(read / 1024.0 / 1024)
        .setDiskWrite(write / 1024.0 / 1024)
        .setTimestamp(new Date().getTime());

    } catch (Exception e) {
      log.error("Fail to read the runtime info, error", e);
    }
    return null;
  }

  private double calculateCpuUsage(CentralProcessor processor, long[] prevTicks) {
    long[] ticks = processor.getSystemCpuLoadTicks();
    long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
      - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
    long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
      - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
    long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
      - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
    long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
      - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
    long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
      - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
    long cUser = ticks[CentralProcessor.TickType.USER.getIndex()]
      - prevTicks[CentralProcessor.TickType.USER.getIndex()];
    long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
      - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
    long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
      - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
    long totalCpu = cUser + nice + cSys + idle + ioWait + irq + softIrq + steal;
    return (cSys + cUser) * 1.0 / totalCpu;
  }

}
