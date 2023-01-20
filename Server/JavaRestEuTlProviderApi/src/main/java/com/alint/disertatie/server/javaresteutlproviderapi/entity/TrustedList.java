package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.TSLType;
import eu.europa.esig.dss.jaxb.object.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrustedList extends Entity{

    private int tslVersionIdentifier;
    private int tslSequenceNumber;
    private TSLType tslType;
    private String SchemeOperatorName;
    private List<PostalAddress> postalAddresses;
    private List<String> electronicAddresses;
    private String schemeName;
    private List<String> schemeInformationURI;
    private String statusDeterminationApproach;
    private String schemeTypeCommunityRules;
    private String schemeTerritory;
    private String PolicyOrLegalNotice;
    private int historicalInformationPeriod;
    private List<OtherTSLPointer> pointersToOtherTSL;
    private String listIssueDateTime;
    private String nextUpdate;
    private String distributionPoint;
    private List<TrustServiceProvider> trustServiceProviders;
}
