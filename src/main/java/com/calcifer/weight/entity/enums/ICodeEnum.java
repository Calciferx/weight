package com.calcifer.weight.entity.enums;

public interface ICodeEnum {
    Integer getCode();

    String getMsg();

    static <E extends Enum<?> & ICodeEnum> E codeOf(Class<E> enumClass, Integer code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode().equals(code)) return e;
        }
        return null;
    }

    static <E extends Enum<?> & ICodeEnum> E ordinalOf(Class<E> enumClass, Integer ordinal) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.ordinal() == ordinal) return e;
        }
        return null;
    }
}
