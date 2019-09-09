package com.github.flycat.module;

import java.util.List;

public interface Module {

    default String getPackageName(){
        return this.getClass().getPackage().getName();
    }

    void configure();

    void addDependency(Class<? extends Module> module);

    List<Class<? extends Module>> getDependencies();
}
