package com.cong.entity.VO.request;

import com.cong.entity.BaseData;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetVO implements BaseData {

  @Email
    @NotEmpty
  String email;
  @Length(min = 6, max = 6)
  String code;
  @Length(min = 6, max = 20)
  String password;
}
