package com.github.flycat.module;

public class B2Module extends AbstractModule{
    @Override
    public void configure() {
        addDependency(B1Module.class);
    }
}
