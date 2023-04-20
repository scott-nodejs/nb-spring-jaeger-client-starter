package com.nb.spring.jaeger.client.starter.jaegerclientstarter.dubbo;

import lombok.Data;

@Data
public class CommonException extends RuntimeException {

    /**
     * 异常错误等级
     */
    private ExceptionSeverityEnum severity = ExceptionSeverityEnum.HIGH;

}
