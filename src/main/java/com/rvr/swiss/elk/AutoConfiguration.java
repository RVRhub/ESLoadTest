package com.rvr.swiss.elk;


import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        // Spring Boot AutoConfigurations
        PropertyPlaceholderAutoConfiguration.class,
        AopAutoConfiguration.class,
        RabbitAutoConfiguration.class,
        CodecsAutoConfiguration.class,
        ProjectInfoAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        ElasticsearchAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
       // MappingsEndpointAutoConfiguration.class,
        RestTemplateAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
     //   WebEndpointAutoConfiguration.class,
        WebClientAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class,
   //     WebMvcMetricsAutoConfiguration.class,


})
public @interface AutoConfiguration {
}
