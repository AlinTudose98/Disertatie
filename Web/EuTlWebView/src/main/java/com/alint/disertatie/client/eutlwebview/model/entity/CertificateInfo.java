package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CertificateInfo {
    private String serialNumber;
    private String signingAlgorithm;
    private String issuer;
    private String subject;
    private String validity;
    private String x509SKI;
    private List<String> keyUsages;
}
