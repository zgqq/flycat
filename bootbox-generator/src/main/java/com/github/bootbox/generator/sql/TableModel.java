package com.github.bootbox.generator.sql;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableModel {
    String alias() default "";
    String name() ;
    Class<?>[] manyToOne() default {};
    boolean isRecordTable() default false;
}
