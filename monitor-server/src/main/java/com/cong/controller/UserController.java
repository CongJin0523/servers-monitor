package com.cong.controller;

import com.cong.entity.RestBean;
import com.cong.entity.VO.request.ChangePasswordVO;
import com.cong.entity.VO.request.CreateSubAccountVO;
import com.cong.entity.VO.request.ModifyEmailVO;
import com.cong.entity.VO.response.SubAccountVO;
import com.cong.service.AccountService;
import com.cong.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Resource
  AccountService accountService;

  @PostMapping("/change-password")
  public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int userId) {

    return  accountService.changePassword(vo.getPassword(), vo.getNew_password(), userId) ?
      RestBean.success() : RestBean.fail(401, "The old password is incorrect.");
  }

  @PostMapping("/modify-email")
  public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                    @RequestBody @Valid ModifyEmailVO vo) {
    String result = accountService.modifyEmail(id, vo);
    if(result == null) {
      return RestBean.success();
    } else {
      return RestBean.fail(401, result);
    }
  }

  @PostMapping("/sub/create")
  public RestBean<Void> createSubAccount(@RequestBody @Valid CreateSubAccountVO vo) {
    accountService.createSubAccount(vo);
    return RestBean.success();
  }

  @GetMapping("/sub/delete")
  public RestBean<Void> deleteSubAccount(int uid,
                                         @RequestAttribute(Const.ATTR_USER_ID) int userId) {
    if (uid == userId) {
      return RestBean.fail(401, "Illegal parameter");
    }
    accountService.deleteSubAccount(uid);
    return RestBean.success();
  }

  @GetMapping("/sub/list")
  public RestBean<List<SubAccountVO>> subAccountList() {
    return RestBean.success(accountService.listSubAccount());
  }
}
