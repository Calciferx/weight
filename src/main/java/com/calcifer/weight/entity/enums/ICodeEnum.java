package com.calcifer.weight.entity.enums;

public interface ICodeEnum {
    int getCode();

    String getMsg();

    static <E extends Enum<?> & ICodeEnum> E codeOf(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code) return e;
        }
        return null;
    }

    static <E extends Enum<?> & ICodeEnum> E ordinalOf(Class<E> enumClass, int ordinal) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.ordinal() == ordinal) return e;
        }
        return null;
    }
}
