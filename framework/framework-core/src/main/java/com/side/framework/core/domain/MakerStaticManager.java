package com.side.framework.core.domain;


import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 构造器管理器
 * 也是此类类型的唯一约束入口
 *
 * @author yxfl
 * @date 2024/09/02 19
 **/
@Slf4j
public class MakerStaticManager {

    private MakerStaticManager() {
    }

    public static class IdMakerManager {
        /**
         * id生成器列表
         */
        private static final List<IdMaker> idMakerList = new ArrayList<>();

        static {
            ServiceLoader<IdMaker> idMakerLoader = ServiceLoader.load(IdMaker.class);
            for (IdMaker idMaker : idMakerLoader) {
                idMakerList.add(idMaker);
                Collections.sort(idMakerList);
            }
        }

        public static void putIdMaker(IdMaker idMaker) {
            Objects.requireNonNull(idMaker, "idMaker can not be null");
            idMakerList.add(idMaker);
            Collections.sort(idMakerList);
        }

        /**
         * 根据类型获取id生成器
         *
         * @param classReferencePath
         * @param isSupportVerify
         * @return
         */
        public static IdMaker getIdMakerByType(String classReferencePath, boolean isSupportVerify) {
            for (IdMaker idMaker : idMakerList) {
                if (idMaker.getIdType().getName().equals(classReferencePath) && idMaker.isSupportVerify() == isSupportVerify) {
                    return idMaker;
                }
            }
            return null;
        }
    }
}
