package com.github.flycat.module;

public class B3Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(B2Module.class);
    }
}
