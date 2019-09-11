package com.github.flycat.module;

public class D2Module extends AbstractModule {

    @Override
    public Class<? extends Module> getDefaultReference() {
        return D2SubModule1.class;
    }

    @Override
    public String getPackageName() {
        return "module.d2";
    }
}
