package com.github.flycat.module;

public class D1SubModule extends AbstractModule {
    @Override
    public void configure() {
        setParent(D1Module.class);
    }

    @Override
    public String getPackageName() {
        return "module.d1.1";
    }
}
