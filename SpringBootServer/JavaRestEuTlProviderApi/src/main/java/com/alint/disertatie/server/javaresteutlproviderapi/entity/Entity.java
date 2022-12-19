package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import eu.europa.esig.dss.jaxb.object.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
    protected String indication;
    protected String subIndication;
    protected List<Message> warnings;
    protected String lastUpdated;
}
