package com.jiebai.framework.influxdb.converter;

import org.apache.commons.collections4.MapUtils;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DTOConverter
 *
 * @author lizhihui
 * @version 1.0.0
 */
public class DTOConverter<T> {

    private Class<T> clazz;

    public DTOConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> convert(QueryResult results) {
        if(!QueryResultConverter.isResultHasData(results)) {
            return null;
        }
        List<T> lists = new ArrayList<>();
        for (QueryResult.Result result : results.getResults()) {

            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series serie : series) {
                Map<String, String> tags = serie.getTags();
                List<List<Object>> values = serie.getValues();
                List<String> columns = serie.getColumns();
                lists.addAll(getQueryData(columns, values, tags));
            }
        }
        return lists;
    }

    private List<T> getQueryData(List<String> columns, List<List<Object>> values, Map<String, String> tags) {
        List<T> lists = new ArrayList();

        for (List<Object> list : values) {
            BeanWrapperImpl bean = new BeanWrapperImpl(clazz);
            if (MapUtils.isNotEmpty(tags)) {
                tags.forEach((key, value) -> {
                    bean.setPropertyValue(setColumns(key), value);
                });
            }
            for (int i = 0; i < list.size(); i++) {
                // 字段名
                String propertyName = setColumns(columns.get(i));
                // 相应字段值
                Object value = list.get(i);
                try {
                    bean.setPropertyValue(propertyName, value);
                } catch (NotWritablePropertyException e) {
                    // 忽略bean中不存在的字段
                }
            }

            lists.add((T)bean.getWrappedInstance());
        }

        return lists;
    }

    private String setColumns(String column) {
        column = column.replace("tag_", "");
        String[] cols = column.split("_");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cols.length; i++) {
            String col = cols[i].toLowerCase();
            if (i != 0) {
                String start = col.substring(0, 1).toUpperCase();
                String end = col.substring(1).toLowerCase();
                col = start + end;
            }
            sb.append(col);
        }
        return sb.toString();
    }

}
