package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITrustServiceAdditionalType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITrustServiceStatus;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITrustServiceType;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STITrustService {
    private STITrustServiceType serviceType;
    private String serviceName;
    private List<STIDigitalId> digitalIds;
    private STITrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<STITrustServiceAdditionalType> additionalTypes;
    private List<STITrustServiceHistoryInstance> serviceHistory;
}
