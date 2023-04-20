package com.nb.spring.jaeger.client.starter.jaegerclientstarter.config;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucong
 * @date 2023/4/14 11:40
 */
@Configuration
@ConditionalOnProperty(value = "nb.tracer", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration.class)
@Slf4j
public class JaegerClientConfig {

    @Bean
    public io.opentracing.Tracer tracer() {
        //获取系统变量
        log.info("load tracer env..");
        String jaeger_service_name = System.getenv("JAEGER_SERVICE_NAME");
        String jaeger_agent_host = System.getenv("JAEGER_AGENT_HOST");

        if(jaeger_service_name != null && jaeger_agent_host != null){
            Integer jaeger_agent_port = Integer.valueOf(System.getenv("JAEGER_AGENT_PORT"));
            Integer jaeger_sampler_param = Integer.valueOf(System.getenv("JAEGER_SAMPLER_PARAM"));
            log.info("load jaeger_service_name: {}", jaeger_service_name);
            log.info("load jaeger_agent_host: {}", jaeger_agent_host);
            log.info("load jaeger_agent_port: {}", jaeger_agent_port);
            log.info("load jaeger_sampler_param:{}", jaeger_sampler_param);
            log.info("tracer load successfully ....");
            io.jaegertracing.Configuration config = new io.jaegertracing.Configuration(jaeger_service_name);
            io.jaegertracing.Configuration.SenderConfiguration sender = new io.jaegertracing.Configuration.SenderConfiguration();

            sender.withAgentHost(jaeger_agent_host);
            sender.withAgentPort(jaeger_agent_port);

            config.withSampler(new io.jaegertracing.Configuration.SamplerConfiguration().withType("const").withParam(jaeger_sampler_param));
            config.withReporter(new io.jaegertracing.Configuration.ReporterConfiguration().withSender(sender).withMaxQueueSize(10000).withLogSpans(true));
            B3TextMapCodec b3Codec = new B3TextMapCodec.Builder().build();
            JaegerTracer tracer = config.getTracerBuilder().registerInjector(Format.Builtin.HTTP_HEADERS, b3Codec)
                    .registerExtractor(Format.Builtin.HTTP_HEADERS, b3Codec)
                    .registerInjector(Format.Builtin.TEXT_MAP, b3Codec)
                    .registerExtractor(Format.Builtin.TEXT_MAP, b3Codec)
                    .build();
            GlobalTracer.registerIfAbsent(tracer);
            return tracer;
        }else{
            log.info("tracer load fail ....");
           return null;
        }
    }
}
