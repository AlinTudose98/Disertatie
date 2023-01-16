package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Entity {
    protected String indication;
    protected String subIndication;
    protected List<CustomMessage> warnings;
    protected String lastUpdated;
}
