package com.cong.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CopyProperties {
  public static void copyProperties(Object source, Object target)  {
    Class<?> sourceClass = source.getClass();
    Class<?> targetClass = target.getClass();
    Field[] sourceFields = sourceClass.getDeclaredFields();
    Field[] targetFields = targetClass.getDeclaredFields();
    Map<String, Field> sourceFieldMap = new HashMap<String, Field>();
    for (Field field : sourceFields) {
      sourceFieldMap.put(field.getName(), field);
    }
    for (Field field : targetFields) {
      if (sourceFieldMap.containsKey(field.getName())) {
        Field sourceField = sourceFieldMap.get(field.getName());
        if (field.getType().equals(sourceField.getType())) {
          sourceField.setAccessible(true);
          field.setAccessible(true);
          try {
            field.set(target, sourceField.get(source));
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }

        }
      }
    }
  }
}
