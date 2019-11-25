package com.jiebai.framework.influxdb.converter;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.influxdb.dto.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MapConverter
 *
 * @author lizhihui
 * @version 1.0.0
 */
public class QueryResultConverter {

    /**
     * 聚合结果使用，如group by 结果
     * key为tag
     * value为聚合计算结果
     *
     * @param results QueryResult
     * @return map
     */
    public static Map<String, Object> convert2Map(QueryResult results) {
        if (!isResultHasData(results)) {
            return null;
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        for (QueryResult.Result result : results.getResults()) {

            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series serie : series) {
                Map<String, String> tags = serie.getTags();
                List<List<Object>> values = serie.getValues();

                String resultMapKey = "";
                Object resultMapValue = null;
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    resultMapKey = entry.getValue();
                }
                for (List<Object> list : values) {
                    for (int i = 0; i < list.size(); i++) {
                        resultMapValue = list.get(i);
                    }
                }
                resultMap.put(resultMapKey, resultMapValue);
            }
        }
        return resultMap;
    }

    /**
     * sum函数结果使用
     *
     * @param results QueryResult
     * @return Integer
     */
    public static Integer convert2Integer(QueryResult results) {
        Double sumResult = 0D;

        if (!isResultHasData(results)) {
            return 0;
        }

        for (QueryResult.Result result : results.getResults()) {
            List<QueryResult.Series> series = result.getSeries();
            QueryResult.Series serie = series.get(0);
            List<List<Object>> values = serie.getValues();
            List<Object> sumValueList = values.get(0);
            sumResult = (Double)sumValueList.get(1);
        }
        return sumResult.intValue();
    }

    /**
     * 判断结果集中是否有数据
     *
     * @param results 结果集
     * @return boolean
     */
    protected static boolean isResultHasData(QueryResult results) {
        if (Objects.isNull(results)) {
            return false;
        }
        List<QueryResult.Result> resultList = results.getResults();
        if (CollectionUtils.isEmpty(resultList)) {
            return false;
        }
        if (Objects.isNull(resultList.get(0).getSeries())) {
            return false;
        }
        return true;
    }
}
