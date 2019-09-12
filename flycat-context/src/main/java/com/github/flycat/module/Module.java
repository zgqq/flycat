package com.github.flycat.module;

import java.util.List;

public interface Module {

    default String getName(){
        return this.getClass().getSimpleName();
    }

    default String getPackageName() {
        return this.getClass().getPackage().getName();
    }

    default void init() {
    }

    Module addDependency(Module module);

    List<Module> getDependencies();

    default Module getDefaultReference() {
        return this;
    }

    Module setParent(Module module);

    Module getParent();

    Module setModuleType(ModuleType moduleType);

    ModuleType getModuleType();
}
