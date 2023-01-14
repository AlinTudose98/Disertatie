package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.TSLType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrustedList extends Entity{
    private String distributionPoint;
    private String schemeTerritory;
    private TSLType tslType;
    private String listIssueDateTime;
    private String nextUpdate;
    private int historicalInformationPeriod;
    private List<TrustServiceProvider> trustServiceProviders;
    private List<OtherTSLPointer> pointersToOtherTSL;
}
