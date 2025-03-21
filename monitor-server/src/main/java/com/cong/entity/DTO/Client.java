package com.cong.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_client")
@AllArgsConstructor
public class Client {
  @TableId
  Integer id;
  String name;
  String token;
  String location;
  String node;
  Date registerTime;
}
