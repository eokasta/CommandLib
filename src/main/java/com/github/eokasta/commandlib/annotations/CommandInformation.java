package com.github.eokasta.commandlib.annotations;

import com.github.eokasta.commandlib.enums.CommandTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInformation {

    String[] name();
    String usage() default "";
    String description() default "";
    String permission() default "";
    @Deprecated
    boolean onlyPlayer() default false;
    CommandTarget target() default CommandTarget.ALL;

}
