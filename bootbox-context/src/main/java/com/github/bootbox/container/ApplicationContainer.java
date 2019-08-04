package com.github.bootbox.container;

public interface ApplicationContainer {

    <T> T getBean(Class<T> clazz);

    Object getBean(String name);

    String getApplicationName();
}
