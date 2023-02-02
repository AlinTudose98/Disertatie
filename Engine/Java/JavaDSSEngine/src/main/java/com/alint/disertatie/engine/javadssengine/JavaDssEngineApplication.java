package com.alint.disertatie.engine.javadssengine;

import com.alint.disertatie.engine.javadssengine.server.STIJavaValidationEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JavaDssEngineApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(JavaDssEngineApplication.class, args);

        STIJavaValidationEngine engine = context.getBean("STIJavaValidationEngine", STIJavaValidationEngine.class);
        try{
            engine.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
