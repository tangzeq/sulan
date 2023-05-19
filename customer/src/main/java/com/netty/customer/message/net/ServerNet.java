package com.netty.customer.message.net;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能描述：服务器信息
 * 作者：唐泽齐
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerNet {
    private String serverhost;
    private Integer serverport;
    private List<NodeNet> remotes;
    private List<NodeNet> nodes;
    private List<NodeNet> channles;
}
