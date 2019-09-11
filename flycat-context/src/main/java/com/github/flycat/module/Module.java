package com.github.flycat.module;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.List;

public interface Module {

    default String getPackageName() {
        return this.getClass().getPackage().getName();
    }

    default void configure() {
    }

    void addDependency(Class<? extends Module> module);

    List<Class<? extends Module>> getDependencies();

    default Class<? extends Module> getDefaultReference() {
        return this.getClass();
    }

    void setParent(Class<? extends Module> module);

    Class<? extends Module> getParent();
}
