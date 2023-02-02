package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.STITSLType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STIListOfTrustedLists extends STIEntity {
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
    private String listIssueDateTime;
    private String nextUpdate;
    private String distributionPoint;
    private List<STIOtherTSLPointer> pointersToOtherTsl;

    public STIOtherTSLPointer getCCTSLPointer(String countryCode) {
        for (STIOtherTSLPointer iter: pointersToOtherTsl) {
            if(countryCode.equalsIgnoreCase(iter.getSchemeTerritory())) {
                return iter;
            }
        }
        return null;
    }
}
