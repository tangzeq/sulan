package com.netty.customer.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 功能描述：索引
 * 作者：唐泽齐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseIndex implements Serializable {
    Set<String> index;

    public void add(String index) {
        if(this.index == null) {
            this.index = new HashSet<String>();
        }
        this.index.add(index);
    }
    public void del(String index) {
        this.index.remove(index);
    }
}
