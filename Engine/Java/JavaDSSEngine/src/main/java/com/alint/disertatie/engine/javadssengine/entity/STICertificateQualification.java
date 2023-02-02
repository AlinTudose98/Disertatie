package com.alint.disertatie.engine.javadssengine.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STICertificateQualification {
    private String label;
    private boolean qualifiedStatus;
    private String type;
    private boolean qscdStatus;
}
