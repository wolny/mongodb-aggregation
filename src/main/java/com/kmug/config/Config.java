package com.kmug.config;

import com.google.common.base.Throwables;
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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import java.net.UnknownHostException;

/**
 * @author Adrian Wolny
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.kmug")
public class Config extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean
    public VelocityConfigurer velocityConfig() {
        VelocityConfigurer velocityConfigurer = new VelocityConfigurer();
        velocityConfigurer.setResourceLoaderPath("/WEB-INF/html/");
        return velocityConfigurer;
    }

    @Bean
    public VelocityViewResolver viewResolver() {
        VelocityViewResolver resolver = new VelocityViewResolver();
        resolver.setSuffix(".html");
        return resolver;
    }

    @Bean
    public AggregatesRepository<NumericalAggregate> aggregatesRepository() {
        try {
            return new MongoAggregatesRepository(mongo());
        } catch (UnknownHostException e) {
            throw Throwables.propagate(e);
        }
    }

    @Bean(destroyMethod = "close")
    public Mongo mongo() throws UnknownHostException {
        return new Mongo(ServerAddress.defaultHost(), mongoOptions());
    }

    @Bean
    public MongoOptions mongoOptions() {
        MongoOptions result = new MongoOptions();
        // adjust write concert for better performance
        result.setWriteConcern(WriteConcern.UNACKNOWLEDGED);
        return result;
    }
}