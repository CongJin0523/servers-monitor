package com.cong.context;

import com.cong.entity.DTO.Account;

public class AccountContext {
  public static ThreadLocal<Account> threadLocal = new ThreadLocal<>();

  public static void set(Account account) {
    threadLocal.set(account);
  }

  public static Account get() {
    return threadLocal.get();
  }

  public static void remove() {
    threadLocal.remove();
  }
}
