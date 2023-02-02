package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.STITrustServiceAdditionalType;
import com.alint.disertatie.client.eutlwebview.model.enums.STITrustServiceStatus;
import com.alint.disertatie.client.eutlwebview.model.enums.STITrustServiceType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
