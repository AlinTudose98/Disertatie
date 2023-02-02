package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITSLType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STITrustedList extends STIEntity {

    private int tslVersionIdentifier;
    private int tslSequenceNumber;
    private STITSLType tslType;
    private String SchemeOperatorName;
    private List<STIPostalAddress> postalAddresses;
    private List<String> electronicAddresses;
    private String schemeName;
    private List<String> schemeInformationURI;
    private String statusDeterminationApproach;
    private String schemeTypeCommunityRules;
    private String schemeTerritory;
    private String PolicyOrLegalNotice;
    private int historicalInformationPeriod;
    private List<STIOtherTSLPointer> pointersToOtherTSL;
    private String listIssueDateTime;
    private String nextUpdate;
    private String distributionPoint;
    private List<STITrustServiceProvider> trustServiceProviders;
}
