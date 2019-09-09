package com.github.flycat.module;

public class C4Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(C2Module.class);
    }

    @Override
    public String getPackageName() {
        return "module.c4";
    }
}
