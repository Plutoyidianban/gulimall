package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {



        @Autowired
        private RestHighLevelClient client;
        @Data
        @ToString
       public static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }


        @org.junit.Test
        public void searchData() throws IOException{
            SearchRequest searchRequest = new SearchRequest();

            searchRequest.indices("bank");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

            TermsAggregationBuilder aggAgg = AggregationBuilders.terms("ageAgg").field("age");
            sourceBuilder.aggregation(aggAgg);

            AvgAggregationBuilder avg=AggregationBuilders.avg("balanceAvg").field("balance");
            sourceBuilder.aggregation(avg);


            System.out.println(sourceBuilder.toString());

            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

            System.out.println(searchResponse.toString());

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                String string = hit.getSourceAsString();
                Account account = JSON.parseObject(string, Account.class);
                System.out.println("account"+account);
            }

            Aggregations aggregations = searchResponse.getAggregations();
            Terms ageAgg = aggregations.get("ageAgg");
            for (Terms.Bucket bucket : ageAgg.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                System.out.println(keyAsString);
            }

        }




        @org.junit.Test
       public void contextLoads() {
            System.out.println(client);
        }

    @org.junit.Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        User user = new User();
        user.setAge(19);
        user.setGender("男");
        user.setUserName("张三");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        // 执行操作
        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        // 提取有用的响应数据
        System.out.println(index);
    }

    @Data
    private class User{
            private String userName;
            private Integer age;
            private  String gender;
    }


}
