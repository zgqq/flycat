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

import com.github.flycat.core.reflect.ReflectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractModule implements Module {
    private List<Module> modules = new ArrayList<>();
    private Module parent;
    private boolean init;
    private ModuleType moduleType = ModuleType.LOCAL;

    public AbstractModule() {
//        this.init();
    }

    @Override
    public void init() {
        if (init) {
            return;
        }
        configure();
        init = true;
    }

    protected void configure() {
    }

    public Module addDependency(Class<? extends Module> module) {
        return addDependency(ReflectUtils.newInstance(module));
    }

    @Override
    public Module addDependency(Module module) {
        if (getClass().equals(module)) {
            throw new RuntimeException("Unable to depend self");
        }
        modules.add(module);
        return this;
    }

    @Override
    public List<Module> getDependencies() {
        return modules;
    }

    public Module setParent(Class<? extends Module> clazz) {
        return setParent(ReflectUtils.newInstance(clazz));
    }

    @Override
    public Module setParent(Module parent) {
        this.parent = parent;
        return this;
    }


    @Override
    public Module getParent() {
        return parent;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    @Override
    public Module setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
        return this;
    }

    @Override
    public ModuleType getModuleType() {
        return moduleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractModule that = (AbstractModule) o;
        return Objects.equals(modules, that.modules) &&
                Objects.equals(parent, that.parent) &&
                moduleType == that.moduleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modules, parent, moduleType);
    }

    @Override
    public Module getDefaultReference() {
        final Class<? extends Module> defaultReferenceClass = getDefaultReferenceClass();
        if (defaultReferenceClass != null) {
            return ReflectUtils.newInstance(defaultReferenceClass);
        }
        return this;
    }

    protected Class<? extends Module> getDefaultReferenceClass() {
        return null;
    }
}
