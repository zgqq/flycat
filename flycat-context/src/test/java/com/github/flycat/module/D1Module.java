package com.github.flycat.module;

public class D1Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(D2Module.class);
    }

    @Override
    public Class<? extends Module> getDefaultReference() {
        return D1SubModule.class;
    }

    @Override
    public String getPackageName() {
        return "module.d1";
    }
}
