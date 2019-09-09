package com.github.flycat.module;

public class A1Module extends AbstractModule {
    @Override
    public void configure() {
        addDependency(A2Module.class);
    }
}
