package com.netty.customer.message.core;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PageHeader {

    /**
     * 列名
     */
    public String Header;
    /**
     * 字段名
     */
    public String accessor;
    /**
     * 是否允许全局搜索 默认否
     */
    public boolean disableGlobalFilter = false;
    /**
     * 是否允许单列搜索 默认否
     */
    public boolean disableFilters = false;
    /**
     * 合并列
     */
    public List<PageHeader> columns;
}
