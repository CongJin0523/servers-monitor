package com.cong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.entity.DTO.Account;
import com.cong.entity.VO.request.ConfirmResetCodeVO;
import com.cong.entity.VO.request.ResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

//UserDetailService is used to get the login info from the database
public interface AccountService extends IService<Account>, UserDetailsService {
  Account findAccountByUsernameOrEmail(String content);
  String verifyCode(String type, String email, String ip);
  String confirmVerifyCode(ConfirmResetCodeVO confirmResetCodeVO);

  String resetPassword(ResetVO resetVO);
}

