package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STIRevocationStatus {
    private Date thisUpdate;
    private Date revocationDate;
    private String revocationReason;
}
