package com.uestc.rpcframeworksimple.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Rpc服务注解
 * 服务端通过此注解可将服务暴露
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface RpcService {
    String version()  default "";
    String group() default "";
}
