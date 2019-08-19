/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.module;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
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
            final Set<String> packageNames = ModuleManager.resolvePackageNames(B1Module.class, new
                    HashMap<>());
            System.out.println(packageNames);
            Assert.assertTrue(false);
        } catch (Exception e) {

        }
    }

    @Test
    public void testPackageNames() {
        final Set<String> packageNames = ModuleManager.resolvePackageNames(C3Module.class, new HashMap<>());
        System.out.println("packageNames " + packageNames);
        Assert.assertTrue(packageNames.size() == 4);
    }

    @Test
    public void testSubModule1() {
        ModuleManager.load(D1SubModule.class);
        final String[] modulePackages = ModuleManager.getModulePackages();
        final ArrayList<String> strings = Lists.newArrayList(modulePackages);
        System.out.println(strings);
        Assert.assertTrue(strings.contains("module.d1.1"));
        Assert.assertTrue(strings.contains("module.d2.1"));
    }


    @Test
    public void testSubModule2() {
        ModuleManager.load(D1Module.class);
        final String[] modulePackages = ModuleManager.getModulePackages();
        final ArrayList<String> strings = Lists.newArrayList(modulePackages);
        System.out.println(strings);
        Assert.assertTrue(strings.contains("module.d1.1"));
        Assert.assertTrue(strings.contains("module.d2.1"));
    }
}
