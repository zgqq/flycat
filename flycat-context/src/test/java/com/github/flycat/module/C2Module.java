package com.github.flycat.module;

public class C2Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(C1Module.class);
    }

    @Override
    public String getPackageName() {
        return "module.c2";
    }
}
