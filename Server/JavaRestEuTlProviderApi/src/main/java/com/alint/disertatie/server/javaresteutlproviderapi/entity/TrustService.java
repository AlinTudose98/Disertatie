package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.TrustServiceAdditionalType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TrustServiceStatus;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TrustServiceType;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrustService {
    private TrustServiceType serviceType;
    private String serviceName;
    private List<DigitalId> digitalIds;
    private TrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<TrustServiceAdditionalType> additionalTypes;
    private List<TrustServiceHistoryInstance> serviceHistory;
}
