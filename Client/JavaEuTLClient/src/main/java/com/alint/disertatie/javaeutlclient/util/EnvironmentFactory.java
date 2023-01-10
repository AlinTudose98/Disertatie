package com.alint.disertatie.javaeutlclient.util;

import java.io.IOException;

public class EnvironmentFactory {
    public static Environment getNewApplicationPropertiesInstance() throws IOException {
        return getNewInstance("properties/application.properties");
    }

    public static Environment getNewInstance(String filePath) throws IOException {
        return new Environment(filePath);
    }
}
