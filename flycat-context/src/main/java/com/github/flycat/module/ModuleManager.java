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

        Map<Class<?>, Class<?>> implementClass = new HashMap<>();
        for (int i = 0; i < modules.length; i++) {
            final Class<? extends Module> module = modules[i];
            try {
                final Module moduleObj = module.newInstance();
                moduleObj.configure();
                final Class<? extends Module> parent = moduleObj.getParent();
                if (parent != null) {
                    implementClass.put(parent, module);
                }
            } catch (Exception e) {
                throw new ModuleException(e);
            }
        }


        final HashSet<String> allPackages = new HashSet<>();
        for (int i = 0; i < modules.length; i++) {
            final Class<? extends Module> module = modules[i];
            final Set<String> packageNames = resolvePackageNames(module, implementClass);
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

    public static Set<String> resolvePackageNames(Class<? extends Module> module) {
        return resolvePackageNames(module, new HashMap<>());
    }

    public static Set<String> resolvePackageNames(Class<? extends Module> module,
                                                  Map<Class<?>, Class<?>> implementModules)
            throws ModuleException {
        final Set<String> packageNames = new HashSet<>();
        final HashSet<Module> objects = new HashSet<>();
        loadModule(null, module, new ArrayList<>(), objects);
        for (Module object : objects) {
            final Class<?> aClass = implementModules.get(object.getClass());
            if (aClass != null) {
                final String name = aClass.getPackage().getName();
                packageNames.add(name);
            } else {
                final Class<? extends Module> defaultReference = object.getDefaultReference();
                if (defaultReference.equals(object.getClass())) {
                    packageNames.add(object.getPackageName());
                } else {
                    try {
                        final Module referModule = defaultReference.newInstance();
                        final String packageName = referModule.getPackageName();
                        packageNames.add(packageName);
                    } catch (Exception e) {
                        throw new ModuleException("Unable to load module", e);
                    }
                }
            }
        }
        return packageNames;
    }

    private static void loadModule(
            Class<? extends Module> parent,
            Class<? extends Module> module,
            final List<Class> objects,
            HashSet<Module> modules
    ) throws ModuleException {
        try {
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
            prepareModule = module.newInstance();
            prepareModule.configure();
            final List<Class<? extends Module>> dependencies = new ArrayList<>(prepareModule.getDependencies());
            final Class<? extends Module> moduleParent = prepareModule.getParent();
            if (moduleParent != null) {
                final Module moduleObj = moduleParent.newInstance();
                moduleObj.configure();
                dependencies.addAll(moduleObj.getDependencies());
            }

            final List<Class> newPath = Lists.newArrayList(objects);
            newPath.add(module);
            modules.add(prepareModule);

            for (Class<? extends Module> dependency : dependencies) {
                loadModule(module, dependency, newPath, modules);
            }
        } catch (Exception e) {
            throw new ModuleException(e);
        }
    }
}
