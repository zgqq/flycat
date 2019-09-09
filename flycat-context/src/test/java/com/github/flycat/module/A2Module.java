package com.github.flycat.module;

public class A2Module extends AbstractModule {
    @Override
    public void configure() {
        addDependency(A1Module.class);
    }
}
