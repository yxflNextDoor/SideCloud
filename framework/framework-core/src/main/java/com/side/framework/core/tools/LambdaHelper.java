package com.side.framework.core.tools;


import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 流式编排工具
 *
 * @author: yxfl
 * @date: 2024/1/24 -16
 * @description LambdaHelper
 */
public class LambdaHelper {
    private LambdaHelper() {
    }

    /**
     * 编排工具暴露端点
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Lambda<T> lambda(@NonNull T t) {
        return new Lambda<T>(t);
    }

    /**
     * 核心编排类
     *
     * @param <T>
     */
    public static class Lambda<T> {

        private T value;

        private Function function;

        private List<Lambda<?>> innerLambadas;

        /**
         * 私有构造器，防止被直接new
         */
        private Lambda() {
        }

        /**
         * 私有化，仅供外部工具类调用
         *
         * @param t
         */
        private Lambda(@NonNull T t) {
            this.value = t;
            //作为初始块的调用，直接返回值本身
            this.function = (v) -> v;
            List<Lambda<?>> lambdas = new ArrayList<>();
            lambdas.add(this);
            this.innerLambadas = lambdas;
        }

        private <T, R> Lambda(Function<T, R> function) {
            this.function = function;
        }

        /**
         * 设置持有值
         *
         * @param t
         */
        private void init(Object t) {
            this.value = (T) t;
        }

        /**
         * 设置内部编排集合
         *
         * @param lambdas
         */
        private void setInnerLambadas(List<Lambda<?>> lambdas) {
            this.innerLambadas = lambdas;
        }

        /**
         * 获取持有值
         *
         * @return
         */
        private T getValue() {
            return value;
        }

        /**
         * 执行编排任务
         *
         * @return
         */
        private T exec() {
            return (T) this.function.apply(getValue());
        }

        /**
         * 编排工具出口
         *
         * @return
         */
        public T compile() {
            while (innerLambadas.size() > 1) {
                Lambda<?> remove = innerLambadas.remove(0);
                innerLambadas.get(0).init(remove.exec());
            }
            Object exec = innerLambadas.get(0).exec();
            //清理集合
            innerLambadas.clear();
            return (T) exec;
        }

        /**
         * 设置编排任务
         *
         * @param function
         * @param <R>
         * @return
         */
        public <R> Lambda<R> thenFunc(@NonNull Function<T, R> function) {
            Lambda<R> lambda = new Lambda<R>(function);
            lambda.setInnerLambadas(innerLambadas);
            innerLambadas.add(lambda);
            return lambda;
        }

        public <R> Lambda<R> thenIfFunc(@NonNull Predicate<T> predicate, @NonNull Function<T, R> function, @NonNull R defaultR) {
            Object compile = innerLambadas.get(innerLambadas.size() - 1).compile();
            if (predicate.test((T) compile)) {
                return LambdaHelper.lambda((T) compile).thenFunc(function);
            }
            //predicate返回false后启用默认值继续
            return LambdaHelper.lambda(defaultR);
        }

    }

}
