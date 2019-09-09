package com.github.flycat.module;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ModuleTests {


    @Test
    public void testCircularDependency() throws Exception {
        try {
            final Set<String> packageNames = ModuleManager.resolvePackageNames(A1Module.class);
            Assert.assertTrue(false);
        } catch (ModuleException exception) {
        }
    }

    @Test
    public void testCircularDependency2() throws Exception {
        try {
            final Set<String> packageNames = ModuleManager.resolvePackageNames(B1Module.class);
            Assert.assertTrue(false);
        } catch (Exception e) {

        }
    }

    @Test
    public void testPackageNames() {
        final Set<String> packageNames = ModuleManager.resolvePackageNames(C3Module.class);
        System.out.println("packageNames " + packageNames);
        Assert.assertTrue(packageNames.size() == 4);
    }
}
