package com.cong.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.cong.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "`db_account`", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class Account implements BaseData {
  @TableId(type = IdType.AUTO)
  Integer id;
  String username;
  String password;
  String email;
  String role;
  Date registerTime;

  @TableField(typeHandler = JacksonTypeHandler.class)
  List<Integer> clients;
}
