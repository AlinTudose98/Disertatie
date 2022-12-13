package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.TSLType;
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
public class ListOfTrustedLists {
    private String distributionPoint;
    private String schemeTerritory;
    private TSLType tslType;
    private Date listIssueDateTime;
    private Date nextUpdate;
    private int historicalInformationPeriod;
    private List<OtherTSLPointer> pointersToOtherTsl;
}
