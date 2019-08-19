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
package com.github.flycat.dispatch;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class JavassistDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistDispatcher.class);

    public static Class generateJobClass(Class<?> listenerInterface, Method method, int index)
            throws NotFoundException, CannotCompileException, ClassNotFoundException {
        ClassPool pool = ClassPool.getDefault();
        final String methodName = method.getName();
        final String className = listenerInterface.getSimpleName() + "Job" + index + "$Proxy";
        CtClass cc = pool.makeClass(listenerInterface.getPackage().getName() + "."
                + className);
        final CtClass ctClass = pool.getCtClass(ExecutorJob.class.getName());
        cc.addInterface(ctClass);

        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            final CtField ctField = CtField.make(parameterType.getName() + " arg" + i + ";", cc);
            cc.addField(ctField);
        }

        final StringBuilder constructorBuilder = new StringBuilder();
        constructorBuilder.append("public " + className + "(");
        insertArguments(parameterTypes, constructorBuilder);
        constructorBuilder.append("){");

        for (int i = 0; i < parameterTypes.length; i++) {
            constructorBuilder.append("this.arg" + i + "=arg" + i + ";");
        }
        constructorBuilder.append("}");

        final String constructString = constructorBuilder.toString();
        final CtConstructor constructor = CtNewConstructor.make(constructString, cc);
        cc.addConstructor(constructor);


        StringBuilder runMethod = new StringBuilder("        public void run(Object executor) {  ");
        runMethod.append("        ((" + listenerInterface.getName() + ") executor).");
        runMethod.append(methodName + "(");
        for (int i = 0; i < parameterTypes.length; i++) {
            runMethod.append("this.arg" + i);
            if (i != parameterTypes.length - 1) {
                runMethod.append(',');
            }
        }
        runMethod.append(");");
        runMethod.append("}");

        final CtMethod ctMethod = CtMethod.make(runMethod.toString(), cc);
        cc.addMethod(ctMethod);
        return cc.toClass(JavassistDispatcher.class.getClassLoader(), JavassistDispatcher.class.getProtectionDomain());
    }


    public static Class generateClass(
            Class<?> listenerInterface) {
        try {
            LOGGER.info("Generating class, {}", listenerInterface.getName());
            ClassPool pool = ClassPool.getDefault();
            final Method[] methods = listenerInterface.getMethods();

            final ArrayList<Class> jobClasses = new ArrayList<>();
            for (int i = 0; i < methods.length; i++) {
                final Method method = methods[i];
                final Class jobClass = generateJobClass(listenerInterface, method, i);
                jobClasses.add(jobClass);
            }

            final CtClass dispatchCtClass =
                    pool.getCtClass(AbstractDispatcher.class.getName());

            CtClass cc = pool.makeClass(listenerInterface.getPackage().getName() + "."
                    + listenerInterface.getSimpleName() + "Dispatch");
            cc.setSuperclass(dispatchCtClass);
            cc.addInterface(pool.getCtClass(listenerInterface.getName()));

            for (int j = 0; j < methods.length; j++) {
                final Method method = methods[j];
                final Class jobClass = jobClasses.get(j);

                final String methodName = method.getName();
                final Class<?> returnType = method.getReturnType();
                final Class<?>[] parameterTypes = method.getParameterTypes();

                StringBuilder methodString = new StringBuilder();
                methodString.append("public ")
                        .append(returnType.getName())
                        .append(" ")
                        .append(methodName)
                        .append("(");

                insertArguments(parameterTypes, methodString);
                methodString.append(") {");


                methodString.append(jobClass.getName() + " job = new " + jobClass.getName() + "(");
                for (int i = 0; i < parameterTypes.length; i++) {
                    methodString.append("arg" + i);
                    if (i != parameterTypes.length - 1) {
                        methodString.append(',');
                    }
                }
                methodString.append(");");

                methodString.append("super.execute(job");

                methodString.append(");");

                methodString.append("}");
                System.out.println("Generated method " + methodString);
                final CtMethod make = CtMethod.make(methodString.toString(), cc);
                cc.addMethod(make);
            }
            return cc.toClass();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create class " + listenerInterface, e);
        }
    }

    private static void insertArguments(Class<?>[] parameterTypes, StringBuilder methodString) {
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            methodString.append(parameterType.getName());
            methodString.append(' ');
            methodString.append("arg" + i);
            if (i != parameterTypes.length - 1) {
                methodString.append(',');
            }
        }
    }
}
