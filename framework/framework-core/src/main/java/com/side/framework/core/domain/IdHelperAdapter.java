package com.side.framework.core.domain;


import com.side.framework.core.tools.IdHelper;

/**
 * id适配器
 *
 * @author yxfl
 * @date 2024/09/02 20
 **/
public class IdHelperAdapter extends AbsIdMaker {
    @Override
    boolean verifyId(String id) {
        return true;
    }

    @Override
    public Object nextId() {
        return IdHelper.getSnowflakeId();
    }

    @Override
    public Boolean isSupportVerify() {
        return false;
    }

    @Override
    public Class<?> getIdType() {
        return Long.class;
    }
}
