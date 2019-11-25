package com.jiebai.framework.influxdb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * influxdb 实体类
 *
 * @author lizhihui
 * @version 1.0.0
 */
@Data
@Builder
public class InfluxDbDTO {

    /**
     * 表名
     */
    private String measurement;
    /**
     * 时间
     */
    private Long timestamp;
    /**
     * 时间单位，默认为毫秒
     */
    @Builder.Default
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    /**
     * 标签键值对
     */
    private Map<String, String> tagMap;
    /**
     * 字段键值对
     */
    private Map<String, Object> fieldMap;
}
