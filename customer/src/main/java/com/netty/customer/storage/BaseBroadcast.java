package com.netty.customer.storage;

import com.netty.customer.message.user.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxProcessor;

import java.io.File;
import java.io.Serializable;
import java.util.Queue;

/**
 * 功能描述：音视频
 * 作者：唐泽齐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseBroadcast extends BaseUser implements Serializable {
    private static final long serialVersionUID = -5613789959948993602L;
    private BaseFile file;
}
