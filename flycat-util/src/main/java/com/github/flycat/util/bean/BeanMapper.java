package com.github.flycat.util.bean;

import org.modelmapper.ModelMapper;

public class BeanMapper {
    private ModelMapper modelMapper = new ModelMapper();

    public <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
