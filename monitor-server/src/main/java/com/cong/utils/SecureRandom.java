package com.cong.utils;

public class SecureRandom {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final java.security.SecureRandom secureRandom = new java.security.SecureRandom();

  public static String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = secureRandom.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }
    System.out.println(sb.toString());
    return sb.toString();
  }

  public static Integer generateRandomInt(int min,int max) {
    return secureRandom.nextInt(max) + min;
  }
}
