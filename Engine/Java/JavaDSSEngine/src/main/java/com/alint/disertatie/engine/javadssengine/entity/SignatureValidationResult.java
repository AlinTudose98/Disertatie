package com.alint.disertatie.engine.javadssengine.entity;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignatureValidationResult {
    private Date signingTime;
    private Date bestSignatureTime;
    private String signedBy;
    private String signatureLevel;
    private List<String> signatureScopes;
    private String signatureFormat;
    private Date extensionPeriodMin;
    private Date extensionPeriodMax;
    private String indication;
    private String subIndication;
    private List<CustomMessage> warnings;
    private List<String> certificateChain;
}