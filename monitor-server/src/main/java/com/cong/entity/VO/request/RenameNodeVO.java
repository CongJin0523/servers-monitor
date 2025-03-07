package com.cong.entity.VO.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RenameNodeVO {
  int id;
  @Length(min = 1, max = 10)
  String node;
  @Pattern(regexp = "(cn|hk|jp|us|sg|kr|de|dk)")
  String location;
}
