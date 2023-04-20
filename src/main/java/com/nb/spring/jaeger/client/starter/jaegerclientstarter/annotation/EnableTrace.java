package com.nb.spring.jaeger.client.starter.jaegerclientstarter.annotation;

import com.nb.spring.jaeger.client.starter.jaegerclientstarter.config.JaegerClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lucong
 * @date 2023/4/17 12:41
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(JaegerClientConfig.class)
@Documented
public @interface EnableTrace {
}
