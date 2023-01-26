package com.example.demodssvalidation;

import com.example.demodssvalidation.server.JavaValidationEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoDssValidationApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoDssValidationApplication.class, args);

        JavaValidationEngine engine = context.getBean("javaValidationEngine", JavaValidationEngine.class);
        try{
        engine.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
