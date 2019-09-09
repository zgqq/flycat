package com.github.flycat.module;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule implements Module {
    private List<Class<? extends Module>> modules = new ArrayList<>();

    @Override
    public void addDependency(Class<? extends Module> module) {
        if (getClass().equals(module)) {
            throw new RuntimeException("Unable to depend self");
        }
        modules.add(module);
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return modules;
    }
}
