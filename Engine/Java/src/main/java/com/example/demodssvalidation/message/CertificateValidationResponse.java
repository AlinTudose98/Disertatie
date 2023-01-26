package com.example.demodssvalidation.message;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CertificateValidationResponse {
    private Date notBefore;
    private Date notAfter;
    private Date validationTime;
    private List<String> keyUsages;
    private String indication;
    private String subIndication;
}