package com.github.flycat.module;

public class D2SubModule1 extends AbstractModule {

    @Override
    public void configure() {
        setParent(D2Module.class);
    }

    @Override
    public String getPackageName() {
        return "module.d2.1";
    }
}
