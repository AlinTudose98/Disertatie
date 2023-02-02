package com.alint.disertatie.client.eutlwebview.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class STISpringAppConfiguration {
    @Bean
    public SimpleDateFormat getParserDateTimeFormat(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    @Bean
    public SimpleDateFormat getPrinterDateTimeFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
