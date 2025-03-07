package com.cong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.entity.DTO.Account;
import com.cong.entity.VO.request.ConfirmResetCodeVO;
import com.cong.entity.VO.request.CreateSubAccountVO;
import com.cong.entity.VO.request.ModifyEmailVO;
import com.cong.entity.VO.request.ResetVO;
import com.cong.entity.VO.response.SubAccountVO;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

//UserDetailService is used to get the login info from the database
public interface AccountService extends IService<Account>, UserDetailsService {
  Account findAccountByUsernameOrEmail(String content);
  String verifyCode(String type, String email, String ip);
  String confirmVerifyCode(ConfirmResetCodeVO confirmResetCodeVO);

  String resetPassword(ResetVO resetVO);

  boolean changePassword(String oldPassword, String newPassword, int userId );

  void createSubAccount(@Valid CreateSubAccountVO vo);

  void deleteSubAccount(int uid);
  List<SubAccountVO> listSubAccount();

  String modifyEmail(int id, @Valid ModifyEmailVO vo);
}

