package com.alint.disertatie.engine.javadssengine.entity;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignedFileValidationResult {
    private ValidationPolicy validationPolicy;
    private int validSignaturesCount;
    private int signaturesCount;
    private List<SignatureValidationResult> signatureResults;
    private Date validationTime;
}