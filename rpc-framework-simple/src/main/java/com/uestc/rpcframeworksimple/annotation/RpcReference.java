package com.uestc.rpcframeworksimple.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 客户端stub注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
    @AliasFor(
            annotation = Component.class
    )
    String version() default "";
    String group() default "";
}
