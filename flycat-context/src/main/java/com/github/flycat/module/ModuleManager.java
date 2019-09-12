package com.github.flycat.module;

import com.alibaba.fastjson.JSON;
import com.github.flycat.core.reflect.ReflectUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class ModuleManager {
    private static volatile Set<String> modulePackages = new HashSet<>();
    private static volatile Set<Module> allModules = new HashSet<>();

    public static void load(Class<? extends Module>... modules) throws ModuleException {
        final List<Module> collect = Arrays.stream(modules)
                .map(ReflectUtils::newInstance).collect(Collectors.toList());
        load(collect.toArray(new Module[]{}));
    }

    public static void load(Module... modules) throws ModuleException {
        if (modules == null || modules.length == 0) {
            return;
        }

        Map<Class<? extends Module>, Module> implementClass = new HashMap<>();
        for (int i = 0; i < modules.length; i++) {
            final Module module = modules[i];
            try {
                module.init();
                final Module parent = module.getParent();
                if (parent != null) {
                    implementClass.put(parent.getClass(), module);
                }
            } catch (Exception e) {
                throw new ModuleException(e);
            }
        }


        final HashSet<Module> allModules = new HashSet<>();
        for (int i = 0; i < modules.length; i++) {
            final Module module = modules[i];
            final Set<Module> needModules = resolveModules(module, implementClass);
            allModules.addAll(needModules);
        }

        final Set<String> allPackages = allModules.stream().map(Module::getPackageName).collect(Collectors.toSet());
        ModuleManager.allModules = allModules;
        ModuleManager.modulePackages = allPackages;
    }

    public static Set<Module> getLocalModules() {
        return allModules.stream()
                .filter(module -> module.getModuleType() == ModuleType.LOCAL)
                .collect(Collectors.toSet());
    }

    public static String[] getLocalModulePackages() {
        return getPackageNamesByModules(getLocalModules());
    }


    public static String[] getPackageNamesOfServiceModules() {
        return getPackageNamesByModules(getServiceModules());
    }


    public static String[] getPackageNamesByModules(Set<Module> modules) {
        return modules.stream().map(Module::getPackageName)
                .collect(Collectors.toList()).toArray(new String[]{});
    }


    public static Set<Module> getServiceModules() {
        return allModules.stream()
                .filter(module -> module.getModuleType() == ModuleType.SERVICE)
                .collect(Collectors.toSet());
    }


    public static Set<Module> getAllModules() {
        return allModules;
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
        return resolvePackageNames(ReflectUtils.newInstance(module));
    }

    public static Set<String> resolvePackageNames(Module module) {
        return resolvePackageNames(module, new HashMap<>());
    }

    public static Set<String> resolvePackageNames(Class<? extends Module> module,
                                                  Map<Class<? extends Module>, Module> implementModules)
            throws ModuleException {
        return resolvePackageNames(ReflectUtils.newInstance(module), implementModules);
    }


    public static Set<String> resolvePackageNames(Module module,
                                                  Map<Class<? extends Module>, Module> implementModules)
            throws ModuleException {
        final Set<Module> modules = resolveModules(module, implementModules);
        return modules.stream().map(Module::getPackageName).collect(Collectors.toSet());
    }

    private static Set<Module> resolveModules(Module module, Map<Class<? extends Module>, Module> implementModules) {
        final Map<Class<?>, Module> moduleMap = new HashMap<>();
        loadModule(null, module, new ArrayList<>(), moduleMap);

        final Set<Map.Entry<Class<?>, Module>> entries = moduleMap.entrySet();

        final Map<Class<?>, Module> moduleReferenceMap = new HashMap<>();
        for (Map.Entry<Class<?>, Module> entry : entries) {
            final Module value = entry.getValue();
            final Module defaultReference = value.getDefaultReference();
            final Module prevModule = moduleReferenceMap.get(defaultReference.getClass());
            if (prevModule != null) {
                if (!prevModule.equals(defaultReference)) {
                    throw new ModuleException("Loaded previous module");
                }
            } else {
                moduleReferenceMap.put(defaultReference.getClass(), defaultReference);
            }
        }

        final Set<Map.Entry<Class<? extends Module>, Module>> implementModule = implementModules.entrySet();
        for (Map.Entry<Class<? extends Module>, Module> classModuleEntry : implementModule) {
            final Class<? extends Module> key = classModuleEntry.getKey();
            final Module value = classModuleEntry.getValue();
            final Module module1 = moduleReferenceMap.get(key);
            if (module1 != null) {
                moduleReferenceMap.put(key, value);
            }
        }
        return moduleReferenceMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    private static void loadModule(
            Module parent,
            Module module,
            final List<Class<? extends Module>> objects,
            Map<Class<?>, Module> modules
    ) throws ModuleException {
        try {
            if (objects.contains(module.getClass())) {
                String parentName = "null";
                if (parent != null) {
                    parentName = parent.getName();
                }
                final List<String> collect = objects.stream().map(Class::getSimpleName).collect(Collectors.toList());
                throw new ModuleException("Found circular dependency " + JSON.toJSONString(collect) + ", but " +
                        parentName + " depend " + module.getName());
            }
            final Module prepareModule = module;
            prepareModule.init();
            final List<Module> dependencies = new ArrayList<>(prepareModule.getDependencies());
            final Module moduleParent = prepareModule.getParent();
            if (moduleParent != null) {
                moduleParent.init();
                dependencies.addAll(moduleParent.getDependencies());
            }

            final List<Class<? extends Module>> newPath = Lists.newArrayList(objects);
            newPath.add(module.getClass());
            final Module prevModule = modules.get(prepareModule.getClass());
            if (prevModule != null) {
                if (!prevModule.equals(prepareModule)) {
                    throw new ModuleException("Loaded previous module " + prevModule + " " + prepareModule);
                }
            } else {
                modules.put(prepareModule.getClass(), prepareModule);
            }


            for (Module dependency : dependencies) {
                loadModule(module, dependency, newPath, modules);
            }
        } catch (Exception e) {
            throw new ModuleException(e);
        }
    }
}
