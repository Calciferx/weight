package com.calcifer.weight.config;

import com.calcifer.weight.entity.enums.ICodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 枚举类转换器 根据code字段值将入参转换为枚举类实例
 */
public class StringToBaseCodeEnumFactory implements ConverterFactory<String, ICodeEnum> {

    @Override
    public <T extends ICodeEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToBaseCodeEnum(targetType);
    }


    private static class StringToBaseCodeEnum<T extends Enum<T> & ICodeEnum> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToBaseCodeEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            int code = Integer.parseInt(source);
            return ICodeEnum.codeOf(enumType, code);
        }
    }

}