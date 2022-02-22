package com.friday.elasticsearch.config;

import com.friday.constant.base.StringConstants;

import lombok.Setter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class RestHighLevelClientConfig {

    @Setter
    private List<String> clusterNodes;

    @Bean
    @ConditionalOnProperty(name = "spring.elasticsearch.rest.enable", havingValue = "false")
    public RestHighLevelClient restHighLevelClient() {
        HttpHost[] hosts = clusterNodes.stream()
                .map(this::buildHttpHost) // eg: new HttpHost("127.0.0.1", 9200, "http")
                .toArray(HttpHost[]::new);
        return new RestHighLevelClient(RestClient.builder(hosts));
    }

    private HttpHost buildHttpHost(String node) {
        String[] nodeInfo = node.split(StringConstants.CHAR_COLON);
        return new HttpHost(nodeInfo[0].trim(), Integer.parseInt(nodeInfo[1].trim()), "http");
    }
}
