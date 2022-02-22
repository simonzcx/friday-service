package com.friday.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.friday.elasticsearch.constant.ESConstants;
import com.friday.elasticsearch.model.BaseDocument;
import com.friday.elasticsearch.service.ElasticSearchService;
import lombok.SneakyThrows;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Resource
    private RestHighLevelClient client;

    @Override
    @SneakyThrows
    public long count(QueryBuilder queryBuilder, String... indices) {
        //构造请求
        CountRequest countRequest = new CountRequest(indices);
        countRequest.query(queryBuilder);

        //执行请求
        return client.count(countRequest, RequestOptions.DEFAULT).getCount();
    }

    @Override
    @SneakyThrows
    public long countDistinct(QueryBuilder queryBuilder, String field, String... indices) {
        //自定义计数去重key，保证上下文一致
        String distinctKey = "distinctKey";

        //构造计数聚合 cardinality：集合中元素的个数
        CardinalityAggregationBuilder aggregationBuilder = AggregationBuilders.cardinality(distinctKey).field(field);

        //构造搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder).aggregation(aggregationBuilder);

        //构造请求
        SearchRequest searchRequest = new SearchRequest(indices);
        searchRequest.source(searchSourceBuilder);

        //执行请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        ParsedCardinality result = searchResponse.getAggregations().get(distinctKey);
        return result.getValue();
    }

    @Override
    @SneakyThrows
    public Map<String, Long> dateHistogram(
            QueryBuilder queryBuilder, String field, DateHistogramInterval interval, String... indices
    ) {

        // 自定义日期聚合key，保证上下文一致
        String dateHistogramKey = "dateHistogramKey";

        //构造聚合
        DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders
                .dateHistogram(dateHistogramKey) //自定义统计名，和下文获取需一致
                .field(field) //日期字段名
                .format("yyyy-MM-dd") //时间格式
                .calendarInterval(interval) //日历间隔，例：1s->1秒 1d->1天 1w->1周 1M->1月 1y->1年 ...
                .minDocCount(0);//最小文档数，比该值小就忽略

        //构造搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder).aggregation(aggregationBuilder).size(0);

        //构造请求
        SearchRequest searchRequest = new SearchRequest(indices);
        searchRequest.source(searchSourceBuilder);
        searchRequest.indicesOptions(IndicesOptions.fromOptions(
                true, //是否忽略不可用索引
                true,   //是否允许索引不存在
                true, //通配符表达式将扩展为打开的索引
                true //通配符表达式将扩展为关闭的索引
        ));

        //执行请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //处理结果
        ParsedDateHistogram dateHistogram = searchResponse.getAggregations().get(dateHistogramKey);
        Iterator<? extends Histogram.Bucket> iterator = dateHistogram.getBuckets().iterator();

        Map<String, Long> result = new HashMap<>();
        while (iterator.hasNext()) {
            Histogram.Bucket bucket = iterator.next();
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    @Override
    @SneakyThrows
    public <T extends BaseDocument> List<T> search(QueryBuilder queryBuilder, Class<T> clazz, String... indices) {
        return this.search(queryBuilder, null, ESConstants.DEFAULT_PAGE_NUM, ESConstants.DEFAULT_PAGE_SIZE, clazz, indices);
    }

    @Override
    @SneakyThrows
    public <T extends BaseDocument> List<T> search(
            QueryBuilder queryBuilder, SortBuilder sortBuilder,
            Integer pageNum, Integer pageSize, Class<T> clazz, String... indices
    ) {
        //构造搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder)
                .sort(sortBuilder)
                .from((pageNum - 1) * pageSize)
                .size(pageSize);

        //构造请求
        SearchRequest searchRequest = new SearchRequest(indices);
        searchRequest.source(searchSourceBuilder);

        //执行请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        //处理结果
        List<T> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            T t = JSON.parseObject(hit.getSourceAsString(), clazz);
            //数据的唯一索引标识
            t.setId(t.getId());
            //索引
            t.setIndex(hit.getIndex());
        }
        return list;
    }


    @SneakyThrows
    public boolean deleteById(String index, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        client.delete(deleteRequest, RequestOptions.DEFAULT);
        return true;
    }
}
