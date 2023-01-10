package com.alint.disertatie.javaeutlclient.util;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Getter
@Setter
public class Environment {
    private Properties properties;

    Environment(String fileName) throws IOException {
        properties = new Properties();
        String string = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(fileName).getPath());
        properties.load(new FileInputStream(string));
    }
}
