package com.cong.entity.VO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class ConfirmResetCodeVO {
  @Email
    @NotEmpty
  String email;
  @Length(min = 6, max = 6)
  String code;
}
