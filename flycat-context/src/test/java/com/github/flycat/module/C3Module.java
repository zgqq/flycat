package com.github.flycat.module;

public class C3Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(C2Module.class);
        addDependency(C4Module.class);
    }

    @Override
    public String getPackageName() {
        return "module.c3";
    }
}
