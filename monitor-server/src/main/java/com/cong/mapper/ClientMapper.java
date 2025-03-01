package com.cong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cong.entity.DTO.Client;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper extends BaseMapper<Client> {
}
