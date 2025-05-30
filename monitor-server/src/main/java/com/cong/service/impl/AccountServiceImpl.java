package com.cong.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.context.AccountContext;
import com.cong.entity.DTO.Account;
import com.cong.entity.VO.request.ConfirmResetCodeVO;
import com.cong.entity.VO.request.CreateSubAccountVO;
import com.cong.entity.VO.request.ModifyEmailVO;
import com.cong.entity.VO.request.ResetVO;
import com.cong.entity.VO.response.SubAccountVO;
import com.cong.mapper.AccountMapper;
import com.cong.service.AccountService;
import com.cong.utils.Const;
import com.cong.utils.FlowUtils;
import com.cong.utils.JwtUtils;
import com.cong.utils.SecureRandom;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

  @Resource
  private FlowUtils flowUtils;

  @Resource
  private JwtUtils jwtUtils;

  @Resource
  private AmqpTemplate amqpTemplate;

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Resource
  private BCryptPasswordEncoder passwordEncoder;

  //use to authenticate login
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = findAccountByUsernameOrEmail(username);
    if(account == null) {
      throw new UsernameNotFoundException("Username or password is incorrect");
    }
    AccountContext.set(account);
    return User
        .withUsername(username)
        .password(account.getPassword())
        .roles(account.getRole())
        .build();
  }

  @Override
  public String verifyCode(String type, String email, String ip) {
    synchronized (ip.intern()) {
      if(!this.verifyLimit(ip)) {
        return "Requesting too frequently, please try again later.";
      }
    }

    int verifyCode = SecureRandom.generateRandomInt(100000,900000);
    Map<String, Object> data = Map.of("type", type, "email", email, "code", verifyCode);
    amqpTemplate.convertAndSend("emailQueue", data);
    stringRedisTemplate.opsForValue()
        .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(verifyCode), 3, TimeUnit.MINUTES);
    log.info("Verify request, type: {} code: {} email: {}", type, verifyCode, email);
    return null;
  }

  @Override
  public String confirmVerifyCode(ConfirmResetCodeVO confirmResetCodeVO) {
    String key = Const.VERIFY_EMAIL_DATA + confirmResetCodeVO.getEmail();
    String verifyCode = stringRedisTemplate.opsForValue().get(key);
    if (verifyCode == null) {
      return "Please to get verification code first";
    }
    if (!verifyCode.equals(confirmResetCodeVO.getCode())) {
      return "Verification code is incorrect";
    }

    return null;

  }

  @Override
  public String resetPassword(ResetVO resetVO) {
    String email = resetVO.getEmail();

    String isValidVerifyCode = confirmVerifyCode(new ConfirmResetCodeVO(email, resetVO.getCode()));
    if (isValidVerifyCode != null) {
      return isValidVerifyCode;
    }

    String password = passwordEncoder.encode(resetVO.getPassword());
    boolean update = this.update().eq("email", email).set("password", password).update();
    if (update) {
      stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
      return null;
    }
    return "Reset password failure, Please try again later.";
  }

  @Override
  public boolean changePassword(String oldPassword, String newPassword, int userId) {
    log.info("change password, old: {}, new: {}, id: {}", oldPassword, newPassword , userId);
    Account account = this.getById(userId);
    String password = account.getPassword();
    if(!passwordEncoder.matches(oldPassword, password))
      return false;
    this.update(Wrappers.<Account>update().eq("id", userId)
      .set("password", passwordEncoder.encode(newPassword)));
    return true;
  }

  @Override
  public void createSubAccount(@Valid CreateSubAccountVO vo){
    Optional.ofNullable(this.findAccountByUsernameOrEmail(vo.getEmail())).ifPresent(account -> {
      throw new IllegalArgumentException("Email already exist");
    });
    Optional.ofNullable(this.findAccountByUsernameOrEmail(vo.getUsername())).ifPresent(account -> {
      throw new IllegalArgumentException("Username already exist");
    });

    Account account = new Account();
    BeanUtils.copyProperties(vo, account);
    account.setPassword(passwordEncoder.encode(vo.getPassword()));
    account.setRole(Const.ROLE_DEFAULT);
    account.setRegisterTime(new Date());
    this.save(account);

  }

  @Override
  public void deleteSubAccount(int uid) {
    if(this.removeById(uid)) {
      jwtUtils.deleteUser(uid);
    }
  }

  @Override
  public List<SubAccountVO> listSubAccount() {
    return this.list(Wrappers.<Account>query().eq("role", Const.ROLE_DEFAULT))
      .stream().map(account -> {
        System.out.println(account);
        SubAccountVO vo = new SubAccountVO();
        BeanUtils.copyProperties(account, vo);
        return vo;
      }).toList();
  }

  @Override
  public String modifyEmail(int id, @Valid ModifyEmailVO vo) {
    String isValidVerifyCode = confirmVerifyCode(new ConfirmResetCodeVO(vo.getEmail(), vo.getCode()));
    if (isValidVerifyCode != null) {
      return isValidVerifyCode;
    }
    Optional<Account> op = Optional.ofNullable(this.findAccountByUsernameOrEmail(vo.getEmail()));
    if (op.isPresent()) {
      return "Email already exist";
    }
    this.update()
      .eq("id", id)
      .set("email", vo.getEmail())
      .update();
    return null;
  }

  // recode the limit to redit, avoid user to request validation code too frequently
  private boolean verifyLimit(String ip) {
    String key = Const.VERIFY_EMAIL_LIMIT + ip;
    return flowUtils.limitOnceCheck(key, 60);
  }

  // mybatisPlus
  public Account findAccountByUsernameOrEmail(String content) {
    return this.query()
        .eq("username", content).or()
        .eq("email", content)
        .one();
  }





}
