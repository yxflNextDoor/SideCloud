package com.side.framework.core.tools;


import com.side.framework.core.constants.CodeEnum;
import com.side.framework.core.exception.BasicException;
import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author: yxfl
 * @date: 2024/1/23 -10
 * @description FuncHelper
 */
public class FuncHelper {
    private FuncHelper() {
    }

    /**
     * 检查参数params,不通过则抛出异常
     *
     * @param params
     * @param predicate
     * @param msg
     */
    public static <T> void preIfThrow(T params, @NonNull Predicate<T> predicate, @NonNull String msg) {
        if (predicate.test(params)) {
            throw new BasicException(CodeEnum.SERVER_ERROR, msg);
        }
    }

    /**
     * 检查参数params,不通过则抛出异常
     *
     * @param params
     * @param predicate
     * @param codeEnum
     */
    public static <T> void preIfThrow(T params, @NonNull Predicate<T> predicate, @NonNull CodeEnum codeEnum) {
        if (predicate.test(params)) {
            throw new BasicException(codeEnum);
        }
    }

    /**
     * 检查参数params,通过则执行function
     *
     * @param params
     * @param predicate
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R preIfFun(T params, @NonNull Predicate<T> predicate, @NonNull Function<T, R> function) {
        if (predicate.test(params)) {
            return function.apply(params);
        }
        return null;
    }

    /**
     * 检查参数params,通过则执行successFunction,不通过则执行failFunction
     *
     * @param params
     * @param predicate
     * @param successFunction
     * @param failFunction
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R preIfFun(T params, @NonNull Predicate<T> predicate, @NonNull Function<T, R> successFunction, @NonNull Function<T, R> failFunction) {
        if (predicate.test(params)) {
            return successFunction.apply(params);
        } else {
            return failFunction.apply(params);
        }
    }

    /**
     * 检查参数params,通过则执行consumer
     *
     * @param params
     * @param predicate
     * @param consumer
     * @param <T>
     */
    public static <T> void preIfConsumer(T params, @NonNull Predicate<T> predicate, @NonNull Consumer<T> consumer) {
        if (predicate.test(params)) {
            consumer.accept(params);
        }
    }


}
