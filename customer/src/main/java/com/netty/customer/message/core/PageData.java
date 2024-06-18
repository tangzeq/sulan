package com.netty.customer.message.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {

    /**
     * 表头
     */
    private List<PageHeader> headers;
    /**
     * 数据
     */
    private List<T> data;

}
