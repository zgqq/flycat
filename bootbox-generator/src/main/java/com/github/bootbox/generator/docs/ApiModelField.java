package com.github.bootbox.generator.docs;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiModelField {
    String value() default "";
}
