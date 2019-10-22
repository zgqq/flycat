package com.github.flycat.util.bean;

public class MapperUtils {
    private static BeanMapper beanMapper = new BeanMapper();

    public static <D> D map(Object source, Class<D> destinationType) {
        return beanMapper.map(source, destinationType);
    }
}
