/*
 *  Copyright (c) 2011-2013 The original author or authors
 *  ------------------------------------------------------
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *       The Eclipse Public License is available at
 *       http://www.eclipse.org/legal/epl-v10.html
 *
 *       The Apache License v2.0 is available at
 *       http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package com.github.flycat.agent.monitor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a setter to be called with the value of a command line argument.
 *
 * @author Clement Escoffier <clement@apache.org>
 * @see Option
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {

    /**
     * The name of this argument (used in doc)
     */
    String argName() default "value";

    /**
     * The (0-based) position of this argument relative to the argument list. The first parameter has the index 0,
     * the second 1...
     * <p/>
     * Index is mandatory to force you to think to the order.
     */
    int index();

    /**
     * Whether or not the argument is required. An argument is required by default.
     */
    boolean required() default true;
}