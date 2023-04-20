package com.nb.spring.jaeger.client.starter.jaegerclientstarter.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import io.opentracing.Span;
import lombok.extern.slf4j.Slf4j;

@Activate(group = {Constants.CONSUMER})
@Slf4j
public class RpcConsumerTraceFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        log.info("QuTraceProviderFilter is called");

        RpcContext rpcContext = RpcContext.getContext();
        DubboTraceUtil.printContextByHeader(rpcContext, "consumer 1");

        Result result = null;
        Span span = null;

        try {
            span = DubboTraceUtil.extractTraceFromLocalCtx(rpcContext);
        } catch (Exception ignored) {
            log.info("failed to extract span from rpcContext", ignored);
        }

        try {
            log.info("dubbo rpc consumer: invoke method ");

            // attach the span context
            try {
                DubboTraceUtil.attachTraceToRemoteCtx(span, rpcContext);
                DubboTraceUtil.printContextByHeader(rpcContext, "consumer 2");
            } catch (Exception ignored) {
            }

            // invoke the dubbo rpc method
            result = invoker.invoke(invocation);

        } catch (RpcException rpcException) {
            log.info("dubbo rpc consumer: received an exception", rpcException);
            if (span != null) {
                //Tags.ERROR.set(span, true);
                span.setTag("error", "1");
                span.setTag("error.code", rpcException.getCode());
                span.setTag("error.message", rpcException.getMessage());
            }
            throw rpcException;
        } finally {

            log.info("dubbo rpc provider: finish the span");
            try {
                // 若consumer返回的结果为异常，则标记为错误-101
                if (result != null && result.getException() != null && span != null) {
                    Throwable e = result.getException();

                    // skip低风险的异常
                    ExceptionSeverityEnum severity = ExceptionSeverityEnum.checkSeverity(e);
                    if (severity == ExceptionSeverityEnum.HIGH) {
                        span.setTag("error", "1");
                        span.setTag("error.code", "-101");
                        span.setTag("error.message", e.toString());
                    }
                }

                if (span != null) span.finish();
            } catch (Exception ignored) {
            }
        }

        log.info("dubbo rpc provider: return result");
        return result;
    }
}
