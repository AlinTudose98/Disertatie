package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DigitalId {
    private String value;
    private String type;
}
