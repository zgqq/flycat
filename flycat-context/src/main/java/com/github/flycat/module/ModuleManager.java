package com.github.flycat.module;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class ModuleManager {

    private static volatile Set<String> modulePackages = new HashSet<>();

    public static void load(Class<? extends Module>... modules) throws ModuleException {
        if (modules == null || modules.length == 0) {
            return;
        }
        final HashSet<String> allPackages = new HashSet<>();
        for (int i = 0; i < modules.length; i++) {
            final Class<? extends Module> module = modules[i];
            final Set<String> packageNames = resolvePackageNames(module);
            allPackages.addAll(packageNames);
        }
        ModuleManager.modulePackages = allPackages;
    }

    public static String[] getModulePackages() {
        return modulePackages.toArray(new String[modulePackages.size()]);
    }

    public static String getModulePackagesAsString(String name) {
        final String[] modulePackages = getModulePackages();
        final StringJoiner stringJoiner = new StringJoiner(",");
        if (StringUtils.isNotBlank(name)) {
            stringJoiner.add(name);
        }
        for (int i = 0; i < modulePackages.length; i++) {
            stringJoiner.add(modulePackages[i]);
        }
        return stringJoiner.toString();
    }

    public static Set<String> resolvePackageNames(Class<? extends Module> module)
            throws ModuleException {
        final HashSet<String> objects = new HashSet<>();
        loadModule(null, module, new ArrayList<>(), objects);
        return objects;
    }

    private static void loadModule(
            Class<? extends Module> parent,
            Class<? extends Module> module,
            final List<Class> objects,
            HashSet<String> packageNames
    ) throws ModuleException {
        if (objects.contains(module)) {
            String parentName = "null";
            if (parent != null) {
                parentName = parent.getSimpleName();
            }
            final List<String> collect = objects.stream().map(Class::getSimpleName).collect(Collectors.toList());
            throw new ModuleException("Found circular dependency " + JSON.toJSONString(collect) + ", but " +
                    parentName + " depend " + module.getSimpleName());
        }
        final Module prepareModule;
        try {
            prepareModule = module.newInstance();
        } catch (Exception e) {
            throw new ModuleException(e);
        }
        prepareModule.configure();
        final String packageName = prepareModule.getPackageName();
        final List<Class<? extends Module>> dependencies = prepareModule.getDependencies();
        final List<Class> newPath = Lists.newArrayList(objects);
        newPath.add(module);
        packageNames.add(packageName);
        for (Class<? extends Module> dependency : dependencies) {
            loadModule(module, dependency, newPath, packageNames);
        }
    }
}
