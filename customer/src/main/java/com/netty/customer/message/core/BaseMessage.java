package com.netty.customer.message.core;

import com.netty.customer.message.user.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：信息头部(所有信息必须继承)
 * 作者：唐泽齐
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessage<T extends BaseUser> implements Serializable {
    //信息编码
    private Long id;
    //类型 0=注册 1=新增 2=移除  5=信息
    private Integer type;
    //通道ID
    private String chanleId;
    //入库日期
    private Long time;
    //信息体类型
    private Class<?> classs;
    //信息体
    private T message;

    public Class<?> getClasss() {
        return this.message.getClass();
    }
}
