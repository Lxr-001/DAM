package org.jeecg.modules.system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cmz
 * @date 2025/8/26
 * @description
 */
@Getter
@AllArgsConstructor
public class NameValuePair {
    private String key;
    private Object value;
}
