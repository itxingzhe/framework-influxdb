package com.jiebai.framework.influxdb.service;

import com.jiebai.framework.influxdb.dto.InfluxDbDTO;
import org.influxdb.dto.QueryResult;

import java.util.List;

/**
 * InfluxService
 *
 * @author lizhihui
 * @version 1.0.0
 */
public interface InfluxDbService {

    /**
     * @param sql sql
     * @return QueryResult
     */
    QueryResult querySql(String sql);

    /**
     * @param sql sql
     * @param database 数据库名
     * @return QueryResult
     */
    QueryResult querySql(String sql, String database);

    /**
     * 创建influx数据库 --
     * influx 会对没有创建的数据库进行创建，如果已经创建则influx会自动忽略；
     * 也可以采用IF NOT EXISTS语法
     * 1. 可能在页面添加数据时创建新数据库，这时在对应service层加入创建influx数据库指令
     * 2. 在项目启动时，flyway初始化之后创建数据库(postConstruct)
     * @param database 数据库名
     */
    void createInfluxDatabase(String database);

    /**
     * 单条插入
     *
     * @param influxDbDTO 数据
     */
    void insert(InfluxDbDTO influxDbDTO);

    /**
     * 指定database单条插入
     *
     * @param influxDbDTO 数据
     * @param database 数据库名
     */
    void insert(InfluxDbDTO influxDbDTO, String database);

    /**
     * 批量插入
     *
     * @param influxDbDTOList 数据list
     */
    void batchInsert(List<InfluxDbDTO> influxDbDTOList);

    /**
     * 指定database批量插入
     *
     * @param influxDbDTOList 数据list
     * @param database 数据库名
     */
    void batchInsert(List<InfluxDbDTO> influxDbDTOList, String database);

    /**
     * 根据where条件条件删除记录
     *
     * @param measurement 表名
     * @param whereClause where子句
     */
    void delete(String measurement, String whereClause);

    /**
     * 指定database，根据where条件条件删除记录
     *
     * @param measurement 表名
     * @param whereClause where子句
     * @param database    database
     */
    void delete(String measurement, String whereClause, String database);

}
