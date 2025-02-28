package com.cong.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface BaseData {
  default <T> T toObject(Class<T> clazz, Consumer<T> consumer) {
    T obj = this.toObject(clazz);
    consumer.accept(obj);
    return obj;
  }
  default <T> T toObject(Class<T> clazz) {
    try {
      Constructor<T> constructor = clazz.getDeclaredConstructor();
      T vo = constructor.newInstance();
      Field[] sourceFields = this.getClass().getDeclaredFields();
      Field[] targetFields = clazz.getDeclaredFields();
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
            field.set(vo, sourceField.get(this));
          }
        }
      }
      return vo;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
