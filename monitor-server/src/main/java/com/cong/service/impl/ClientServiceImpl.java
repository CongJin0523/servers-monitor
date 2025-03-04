package com.cong.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.entity.DTO.Client;
import com.cong.entity.DTO.ClientDetail;
import com.cong.entity.VO.request.ClientDetailVO;
import com.cong.entity.VO.request.RenameClientVO;
import com.cong.entity.VO.request.RuntimeDetailVO;
import com.cong.entity.VO.response.RuntimeHistoryVO;
import com.cong.entity.VO.response.ClientDetailsVO;
import com.cong.entity.VO.response.ClientPreviewVO;
import com.cong.mapper.ClientDetailMapper;
import com.cong.mapper.ClientMapper;
import com.cong.service.ClientService;
import com.cong.utils.InfluxDBUtils;
import com.cong.utils.SecureRandom;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientServiceImpl extends ServiceImpl<ClientMapper ,Client> implements ClientService {

  private String registerToken = SecureRandom.generateRandomString(24);
  /**
   * to save all client info
    */
  private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();
  private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();

  @Resource
  private ClientDetailMapper clientDetailMapper;
  /**
   * init
   * add all client to cache
   */

  @Resource
  private InfluxDBUtils influxDBUtils;

  @PostConstruct
  public void init() {
    clientIdCache.clear();
    clientTokenCache.clear();
    this.list().forEach(this::addClientCache);
  }

  /**
   * check the token and register new client
   * @param token token get from the http header
   * @return if register success
   */
  @Override
  public boolean verifyAndRegister(String token) {
    if (this.registerToken.equals(token)) {
      int id = SecureRandom.generateRandomInt(10000000,90000000);
      Client client = new Client(id, "unnamed", token,"cn", "unnamed", new Date());
      if (this.save(client)) {
        log.info("register new client success {}", client);
        this.registerToken = SecureRandom.generateRandomString(24);
        this.addClientCache(client);
        return true;
      }
    }
    return false;
  }

  /**
   * get register Token
   * @return token use to verify
   */
  @Override
  public String getRegisterToken() {
    return registerToken;
  }

  @Override
  public Client FindClientById(int id) {
    return this.clientIdCache.get(id);
  }

  @Override
  public Client findClientByToken(String token) {
    return this.clientTokenCache.get(token);
  }

  @Override
  public void updateClientDetail(ClientDetailVO vo, Client client) {
    ClientDetail clientDetail = new ClientDetail();
    BeanUtils.copyProperties(vo, clientDetail);
    clientDetail.setId(client.getId());
    if(Objects.nonNull(clientDetailMapper.selectById(clientDetail.getId()))){
      clientDetailMapper.updateById(clientDetail);
    } else {
      clientDetailMapper.insert(clientDetail);
    }
  }

  private  Map<Integer, RuntimeDetailVO> currentRuntimes = new ConcurrentHashMap<>();
  @Override
  public void updateRuntimeDetail(@Valid RuntimeDetailVO vo, Client client) {
    log.info("update runtime detail form client {}", client.getId() );
    log.info("{}", vo);
    currentRuntimes.put(client.getId(), vo);

    influxDBUtils.writeRuntimeData(client.getId(), vo);


  };


  @Override
  public List<ClientPreviewVO> getClientList(){
    return clientIdCache.values().stream()
      .map(client -> {
        ClientPreviewVO vo = new ClientPreviewVO();
        BeanUtils.copyProperties(client, vo);
        BeanUtils.copyProperties(clientDetailMapper.selectById(client.getId()), vo);
        RuntimeDetailVO runtimeDetailVO = currentRuntimes.get(client.getId());
        if (Objects.nonNull(runtimeDetailVO) && System.currentTimeMillis() - runtimeDetailVO.getTimestamp() < 60 *1000) {
          BeanUtils.copyProperties(runtimeDetailVO, vo);
          vo.setOnline(true);
        }
        return vo;
      }).collect(Collectors.toList());
  }

  @Override
  public void renameClient(@Valid RenameClientVO vo){
    this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));
    this.init();
  }

  @Override
  public ClientDetailsVO clientDetails(int clientId){
    ClientDetailsVO vo = new ClientDetailsVO();
    BeanUtils.copyProperties(this.clientIdCache.get(clientId), vo);
    BeanUtils.copyProperties(clientDetailMapper.selectById(clientId), vo);
    vo.setOnline(this.isOnline(currentRuntimes.get(clientId)));
    return vo;
  }

  @Override
  public RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId){
    RuntimeHistoryVO vo = influxDBUtils.readReamtimeData(clientId);
    ClientDetail detail = clientDetailMapper.selectById(clientId);
    BeanUtils.copyProperties(detail, vo);
    return vo;
  }
  @Override
  public RuntimeDetailVO clientRuntimeDetailsNow(int clientId){
    return currentRuntimes.get(clientId);
  }
  private boolean isOnline(RuntimeDetailVO runtime){
    return runtime != null && System.currentTimeMillis() - runtime.getTimestamp() < 60 * 1000;
  }
  /**
   * add new client to cache
   * @param client
   */
  private void addClientCache(Client client) {
    clientIdCache.put(client.getId(), client);
    clientTokenCache.put(client.getToken(), client);
  }

}
