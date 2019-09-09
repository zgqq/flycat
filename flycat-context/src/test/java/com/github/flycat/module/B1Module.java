package com.github.flycat.module;

public class B1Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(B3Module.class);
    }
}
