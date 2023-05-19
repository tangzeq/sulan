package com.netty.customer.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netty.customer.message.core.BaseMessage;
import com.netty.customer.message.user.BaseUser;

/**
 * 功能描述：信息工具类
 * 作者：唐泽齐
 */
public class MessageUtils {

    /**
     * 传输的信息解析回 BaseMessage
     * @param msg
     * @return
     * @throws Exception
     */
    public static BaseMessage resolve(Object msg) throws Exception {
        JSONObject jsonObject = JSON.parseObject(msg.toString());
        String classs = jsonObject.getString("classs");
        Class<?> className = Class.forName(classs);
        String message = jsonObject.getString("message");
        BaseUser baseUser = (BaseUser) JSON.parseObject(message).toJavaObject(className);
        BaseMessage<BaseUser> bm = BaseMessage
                .builder()
                .id(jsonObject.getLong("id"))
                .type(jsonObject.getInteger("type"))
                .chanleId(jsonObject.getString("chanleId"))
                .time(jsonObject.getLong("time"))
                .classs(className)
                .message(baseUser)
                .build();
        return bm;
    }
}
