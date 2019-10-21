package com.github.flycat.spi.redis;

import org.junit.Test;

public class TypeTests {

    @Test
    public void testType() {
        final Class<TypeTests> typeTestsClass = TypeTests.class;
        final String typeName = typeTestsClass.getTypeName();
        System.out.printf(typeName);
    }
}
