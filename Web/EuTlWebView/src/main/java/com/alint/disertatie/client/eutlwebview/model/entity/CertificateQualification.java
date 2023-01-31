package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CertificateQualification {
    private String label;
    private boolean qualifiedStatus;
    private String type;
    private boolean qscdStatus;
}