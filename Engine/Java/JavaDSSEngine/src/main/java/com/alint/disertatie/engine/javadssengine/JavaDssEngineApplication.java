package com.alint.disertatie.engine.javadssengine;

import com.alint.disertatie.engine.javadssengine.server.JavaValidationEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JavaDssEngineApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(JavaDssEngineApplication.class, args);

        JavaValidationEngine engine = context.getBean("javaValidationEngine", JavaValidationEngine.class);
        try{
            engine.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
