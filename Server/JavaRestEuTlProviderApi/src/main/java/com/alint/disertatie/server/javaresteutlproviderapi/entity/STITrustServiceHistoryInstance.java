package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITrustServiceAdditionalType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITrustServiceStatus;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITrustServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STITrustServiceHistoryInstance {
    private STITrustServiceType serviceType;
    private String serviceName;
    private List<STIDigitalId> digitalIds;
    private STITrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<STITrustServiceAdditionalType> additionalTypes;

    public STITrustServiceHistoryInstance(STITrustService service) {
        this.serviceType = service.getServiceType();
        this.serviceName = service.getServiceName();
        this.digitalIds = service.getDigitalIds();
        this.serviceStatus = service.getServiceStatus();
        this.statusStartingTime = service.getStatusStartingTime();
        this.additionalTypes = service.getAdditionalTypes();
    }
}
