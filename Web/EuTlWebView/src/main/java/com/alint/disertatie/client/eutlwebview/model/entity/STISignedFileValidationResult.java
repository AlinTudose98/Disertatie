package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STISignedFileValidationResult {
    private STIValidationPolicy validationPolicy;
    private int validSignaturesCount;
    private int signaturesCount;
    private List<STISignatureValidationResult> signatureResults;
    private Date validationTime;
}
