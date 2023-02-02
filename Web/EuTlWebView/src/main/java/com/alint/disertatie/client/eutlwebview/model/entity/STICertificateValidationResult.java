package com.alint.disertatie.client.eutlwebview.model.entity;

import eu.europa.esig.dss.simplecertificatereport.jaxb.XmlSubject;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STICertificateValidationResult {
    private String subject;
    private Date notBefore;
    private Date notAfter;
    private Date validationTime;
    private List<String> keyUsages;
    private String indication;
    private String subIndication;

    private STICertificateQualification qualificationAtValidation;
    private STICertificateQualification qualificationAtIssuance;
    private STIRevocationStatus revocationStatus;


    public void setSubject(String subject){
        this.subject = subject;
    }

    public void setSubject(XmlSubject subject) {
        this.subject =
                (subject.getCountry()!=null ? "C=" + subject.getCountry() + ", " : "")
                        + (subject.getState()!=null ? "ST=" + subject.getState() + ", ": "")
                        + (subject.getLocality()!=null ? "L=" + subject.getLocality() + ", ": "")
                        + (subject.getOrganizationName()!=null ? "O=" + subject.getOrganizationName() + ", " : "")
                        + (subject.getOrganizationUnit()!=null ? "OU=" + subject.getOrganizationUnit() + ", ": "")
                        + (subject.getEmail()!=null ? "emailAddress=" + subject.getEmail() + ", " : "");
        this.subject = this.subject.substring(0,this.subject.length() - 2);
    }
}
