package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STIEntity {
    protected String indication;
    protected String subIndication;
    protected List<STICustomMessage> warnings;
    protected String lastUpdated;
}
