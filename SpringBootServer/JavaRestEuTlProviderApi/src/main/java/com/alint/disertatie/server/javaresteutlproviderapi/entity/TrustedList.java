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
    private String distributionPoint;
    private TSLType tslType;
    private String listIssueDateTime;
    private String nextUpdate;
    private List<TrustServiceProvider> trustServiceProviders;
    private List<OtherTSLPointer> pointersToOtherTSL;
}
