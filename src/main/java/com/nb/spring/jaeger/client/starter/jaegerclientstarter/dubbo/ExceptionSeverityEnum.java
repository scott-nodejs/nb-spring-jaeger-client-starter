package com.nb.spring.jaeger.client.starter.jaegerclientstarter.dubbo;

public enum ExceptionSeverityEnum {

    HIGH(1, "高风险"),
    MEDIUM(5, "中风险"),
    LOW(10, "低风险");

    private Integer level;
    private String description;

    private ExceptionSeverityEnum(Integer level, String description) {
        this.level = level;
        this.description = description;
    }

    public static ExceptionSeverityEnum checkSeverity(Throwable e) {
        ExceptionSeverityEnum severity = null;
        if (e instanceof CommonException) {
            CommonException que = (CommonException) e;
            severity = que.getSeverity();
        }

        // 默认为HIGH
        if (severity == null) {
            severity = ExceptionSeverityEnum.HIGH;
        }

        return severity;
    }

}
