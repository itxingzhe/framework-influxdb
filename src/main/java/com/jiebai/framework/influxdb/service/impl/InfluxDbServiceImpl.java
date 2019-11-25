package com.jiebai.framework.influxdb.service.impl;

import com.jiebai.framework.influxdb.config.InfluxConfig;
import com.jiebai.framework.influxdb.dto.InfluxDbDTO;
import com.jiebai.framework.influxdb.service.InfluxDbService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * InfluxServiceImpl
 *
 * @author lizhihui
 * @version 1.0.0
 */
@Service
@Slf4j
public class InfluxDbServiceImpl implements InfluxDbService {
    @Resource
    private InfluxDB influxDB;
    @Resource
    private InfluxConfig influxConfig;

    @Override
    public QueryResult querySql(String sql) {
        return querySql(sql, influxConfig.getInfluxDatabase());
    }

    @Override
    public QueryResult querySql(String sql, String database) {
        Query query = new Query(sql, database);
        QueryResult result = influxDB.query(query);
        log.info(result.toString());
        return result;
    }

    /**
     * 创建influx数据库 --
     * influx 会对没有创建的数据库进行创建，如果已经创建则influx会自动忽略；
     * 也可以采用IF NOT EXISTS语法
     * 1. 可能在页面添加数据时创建新数据库，这时在对应service层加入创建influx数据库指令
     * 2. 在项目启动时，flyway初始化之后创建数据库(postConstruct)
     */
    @Override
    public void createInfluxDatabase(String databaseName) {
        Query query = new Query("CREATE DATABASE " + databaseName, databaseName);
        influxDB.query(query);
    }

    @Override
    public void insert(InfluxDbDTO influxDbDTO) {
        insert(influxDbDTO, influxConfig.getInfluxDatabase());
    }

    @Override
    public void insert(InfluxDbDTO influxDbDTO, String database) {
        TimeUnit timeUnit =
            Objects.isNull(influxDbDTO.getTimeUnit()) ? TimeUnit.MILLISECONDS : influxDbDTO.getTimeUnit();
        Point.Builder builder = Point.measurement(influxDbDTO.getMeasurement()).tag(influxDbDTO.getTagMap())
            .fields(influxDbDTO.getFieldMap()).time(influxDbDTO.getTimestamp(), timeUnit);
        influxDB.write(database, "", builder.build());
    }

    @Override
    public void batchInsert(List<InfluxDbDTO> influxDbDTOList) {
        batchInsert(influxDbDTOList, influxConfig.getInfluxDatabase());
    }

    @Override
    public void batchInsert(List<InfluxDbDTO> influxDbDTOList, String database) {
        if (CollectionUtils.isEmpty(influxDbDTOList)) {
            return;
        }
        BatchPoints batchPoints = BatchPoints.database(database).consistency(InfluxDB.ConsistencyLevel.ALL).build();
        influxDbDTOList.forEach(influxDbDTO -> batchPoints.point(
            Point.measurement(influxDbDTO.getMeasurement()).tag(influxDbDTO.getTagMap())
                .fields(influxDbDTO.getFieldMap()).time(influxDbDTO.getTimestamp(), influxDbDTO.getTimeUnit())
                .build()));
        influxDB.write(batchPoints);
    }

    @Override
    public void delete(String measurement, String whereClause) {
        delete(measurement, whereClause, influxConfig.getInfluxDatabase());
    }

    @Override
    public void delete(String measurement, String whereClause, String database) {
        Assert.notNull(measurement, "measurement must not null");
        Assert.notNull(whereClause, "where clause must not null");
        Query query = new Query("DELETE FROM " + measurement + " WHERE " + whereClause, database);
        influxDB.query(query);
    }
}
