package com.netty.customer.storage;

import com.netty.customer.message.user.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 功能描述：对象桶
 * 作者：唐泽齐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMemory<T extends BaseUser> implements Serializable {
    private Long index;
    private T bs;
    private ConcurrentHashMap memory;
}
