package com.cong.utils;

public class Const {
  //redis
  public static final String JWT_BLACK_LIST = "jwt:blacklist:";
  public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
  public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
  public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
  public static final String FLOW_LIMIT_BLOCK = "flow:block:";
  //email type
  public static final String REGISTER_EMAIL = "register";
  public static final String RESET_EMAIL = "reset";

  public final static String ROLE_DEFAULT = "user";

  //custom request attribute
  public final static String ATTR_USER_ID = "id";
  public final static String ATTR_CLIENT = "client";

  //filter order
  public final static int ORDER_FLOW_LIMIT = -101;
  public final static int ORDER_CORS = -102;
}
