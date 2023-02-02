package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.STITSLType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
