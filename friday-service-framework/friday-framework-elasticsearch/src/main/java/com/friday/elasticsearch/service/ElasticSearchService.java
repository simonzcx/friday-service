package com.friday.elasticsearch.service;

import com.friday.elasticsearch.model.BaseDocument;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;
import java.util.Map;

public interface ElasticSearchService {

    /**
     * 统计
     *
     * @param queryBuilder 查询条件
     * @param indices      索引名称
     * @return 统计值
     */
    long count(QueryBuilder queryBuilder, String... indices);


    /**
     * 去重统计
     *
     * @param queryBuilder 查询条件
     * @param field        聚合字段，如：登录日志的 date 字段
     * @param indices      索引名称
     * @return 统计值
     */
    long countDistinct(QueryBuilder queryBuilder, String field, String... indices);


    /**
     * 日期统计
     *
     * @param queryBuilder 查询条件
     * @param field        聚合字段，如：登录日志的 date 字段
     * @param interval     统计时间间隔，如：1天、1周
     * @param indices      索引名称
     * @return 统计值
     */
    Map<String, Long> dateHistogram(QueryBuilder queryBuilder, String field, DateHistogramInterval interval, String... indices);

    /**
     * 默认分页检索
     *
     * @param queryBuilder 查询条件
     * @param clazz        返回值类型
     * @param indices      索引名称
     * @param <T>          返回值类型泛型
     * @return 分页检索数据
     */
    <T extends BaseDocument> List<T> search(QueryBuilder queryBuilder, Class<T> clazz, String... indices);


    /**
     * 分页检索
     *
     * @param queryBuilder 查询条件
     * @param sortBuilder  排序类型
     * @param pageNum      当前页
     * @param pageSize     页大小
     * @param clazz        返回值类型
     * @param indices      索引名称
     * @param <T>          返回值类型泛型
     * @return 分页检索数据
     */
    <T extends BaseDocument> List<T> search(QueryBuilder queryBuilder, SortBuilder sortBuilder, Integer pageNum, Integer pageSize, Class<T> clazz, String... indices);

    /**
     * 根据ID删除
     *
     * @param index 索引名称
     * @param id    ID
     * @return 删除是否成功
     */
    boolean deleteById(String index, String id);
}
