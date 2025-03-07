package com.cong.controller;

import com.cong.entity.RestBean;
import com.cong.entity.VO.request.ConfirmResetCodeVO;
import com.cong.entity.VO.request.ResetVO;
import com.cong.service.AccountService;
import com.cong.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@Validated
public class AuthorizeController {

  @Resource
  AccountService accountService;

  @GetMapping("/verify-code")
  public RestBean<?> verifyCode(@RequestParam("email") @Email @NotEmpty String email,
                                @RequestParam("type") @Pattern(regexp = "(" + Const.RESET_EMAIL + "|" + Const.Change_EAMIL+ ")") String type,
                                HttpServletRequest request) {
    log.info("Get verify code with email:{},type:{}", email, type);
    return this.resultHandle(() -> accountService.verifyCode(type, email, request.getRemoteAddr()));
  }



  @PostMapping("/confirm-verify-code")
  public RestBean<?> confirmCode(@RequestBody @Valid ConfirmResetCodeVO confirmResetCodeVO) {
    return resultHandle(() -> accountService.confirmVerifyCode(confirmResetCodeVO));
  }

  @PostMapping("/reset-password")
  public RestBean<?> resetPassword(@RequestBody @Valid ResetVO resetVO) {
    return resultHandle(resetVO, accountService::resetPassword);
  }

  private <T> RestBean<?> resultHandle(T vo, Function<T, String> function) {
    return resultHandle(() -> function.apply(vo));
  }

  private RestBean<?> resultHandle(Supplier<String> action) {
    String result = action.get();
    return result == null ? RestBean.success() : RestBean.fail(400, result);
  }
}

