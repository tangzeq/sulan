package com.netty.customer.storage;

import com.netty.customer.message.user.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述：链桶
 * 作者：唐泽齐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseLink<T extends BaseUser> {
    private Long index;
    private T bs;
    private BaseLink next;
}
