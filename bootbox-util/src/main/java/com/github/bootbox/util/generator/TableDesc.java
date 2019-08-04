package fm.xiaoai.voss.bops.biz.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableDesc {
    String[] otherTablePrefixes() default {};
}
