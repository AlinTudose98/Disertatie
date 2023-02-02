package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STIEntity {
    protected String indication;
    protected String subIndication;
    protected List<STICustomMessage> warnings;
    protected String lastUpdated;
}
