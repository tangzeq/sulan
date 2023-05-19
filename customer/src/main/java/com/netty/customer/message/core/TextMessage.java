package com.netty.customer.message.core;

import com.netty.customer.message.user.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 功能描述：文本信息
 * 作者：唐泽齐
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextMessage extends BaseUser implements Serializable {
    /**
     * 入库信息
     */
    private String message;
}
