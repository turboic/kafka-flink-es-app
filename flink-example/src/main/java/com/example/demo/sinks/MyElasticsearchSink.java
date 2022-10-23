package com.example.demo.sinks;


import com.alibaba.fastjson.JSON;
import com.example.demo.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class MyElasticsearchSink extends RichSinkFunction<String> implements SinkFunction<String> {
    private static final Logger logger = LoggerFactory.getLogger(MyElasticsearchSink.class);
    private static BulkProcessor bulkProcessor = null;
    private static RestHighLevelClient client = null;

    static {
        CredentialsProvider credentialsProvider = null;
        if (StringUtils.isNotBlank(Constant.username) && StringUtils.isNotBlank(Constant.password)) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(Constant.username, Constant.password));
        }

        RestClientBuilder builder;
        if (credentialsProvider != null) {
            CredentialsProvider finalCredentialsProvider = credentialsProvider;
            builder = RestClient.builder(new HttpHost(Constant.host, Constant.port))
                    .setHttpClientConfigCallback(httpClientBuilder ->
                            httpClientBuilder.setDefaultCredentialsProvider(finalCredentialsProvider));
        } else {
            builder = RestClient.builder(new HttpHost(Constant.host, Constant.port));
        }

        RestClientBuilder.RequestConfigCallback configCallback = requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(5000 * 1000)
                .setSocketTimeout(6000 * 1000);
        builder.setRequestConfigCallback(configCallback);

        client = new RestHighLevelClient(builder);

        BulkProcessor.Builder bulkBuilder = BulkProcessor.builder(((bulkRequest, bulkResponseActionListener) ->
                client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkResponseActionListener)), new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                System.err.println("Try to insert data number : " + request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request,
                                  BulkResponse response) {
                System.err.println("************** Success insert data number : "
                        + request.numberOfActions() + " , id: " + executionId);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                System.err.println("Bulk is unsuccess : " + failure + ", executionId: " + executionId);
            }
        }, "es");

        System.err.println("init BulkProcessor builder ...");

        bulkBuilder.setBulkActions(5);
        bulkBuilder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
        bulkBuilder.setFlushInterval(TimeValue.timeValueSeconds(10));
        bulkBuilder.setConcurrentRequests(8);
        bulkBuilder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1), 3));
        bulkProcessor = bulkBuilder.build();
        System.err.println("bulkProcessor build ...");
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        System.err.println("open Configuration ...");
        super.open(parameters);
    }


    @Override
    public void invoke(String value, Context context) throws Exception {
        System.err.println(value);
        logger.error("测试数据{}",value);
        logger.info("invoke ... ");
        IndexRequest request = new IndexRequest("demo" + DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        request.id(StringUtils.remove(UUID.randomUUID().toString(), "-"));
        Map map = JSON.parseObject(value, Map.class);
        request.source(map, XContentType.JSON);
        bulkProcessor.add(request);
        System.err.println("elasticsearch索引执行插入操作了:{}"+JSON.toJSONString(value));
        super.invoke(value, context);
    }
}