package com.rocketchat.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MoveToRest {
    String method() default "";
    String[] methods() default {};

    boolean discuss() default false;
}
