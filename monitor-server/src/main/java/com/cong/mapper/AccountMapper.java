package com.cong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cong.entity.DTO.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
