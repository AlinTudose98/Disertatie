package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.TSLType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfTrustedLists extends Entity{
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
    private String listIssueDateTime;
    private String nextUpdate;
    private String distributionPoint;
    private List<OtherTSLPointer> pointersToOtherTsl;

    public OtherTSLPointer getCCTSLPointer(String countryCode) {
        for (OtherTSLPointer iter: pointersToOtherTsl) {
            if(countryCode.equalsIgnoreCase(iter.getSchemeTerritory())) {
                return iter;
            }
        }
        return null;
    }
}
