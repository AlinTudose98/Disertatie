package com.alint.disertatie.javaeutlclient.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
    protected String indication;
    protected String subIndication;
    protected List<CustomMessage> warnings;
    protected String lastUpdated;
}
