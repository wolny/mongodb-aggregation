package com.kmug.config;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.kmug.aggregate.NumericalAggregate;
import com.kmug.dao.AggregatesRepository;
import com.kmug.dao.MongoAggregatesRepository;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Adrian Wolny
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.kmug")
public class Config {

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/view/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public AggregatesRepository<NumericalAggregate> aggregatesRepository() {
        return new MongoAggregatesRepository(mongo());
    }

    @Bean(destroyMethod = "close")
    public Mongo mongo() {
        return new Mongo(parseReplicaSet("127.0.0.1:27017"), mongoOptions());
    }

    @Bean
    public MongoOptions mongoOptions() {
        MongoOptions result = new MongoOptions();
        result.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return result;
    }

    private List<ServerAddress> parseReplicaSet(String replicaSet) {
        return Lists.newArrayList(Iterators.transform(Splitter.on(',').trimResults().split(replicaSet).iterator(), new Function<String, ServerAddress>() {
            @Override
            public ServerAddress apply(String input) {
                return parseReplicaSetSeed(input);
            }
        }));
    }

    private ServerAddress parseReplicaSetSeed(String input) {
        String[] split = input.split(":");
        if (split.length < 2) {
            throw new IllegalArgumentException("Cannot parse server address: " + input);
        }
        ServerAddress result = null;
        try {
            result = new ServerAddress(split[0], Integer.valueOf(split[1]));
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }
        return result;
    }

}