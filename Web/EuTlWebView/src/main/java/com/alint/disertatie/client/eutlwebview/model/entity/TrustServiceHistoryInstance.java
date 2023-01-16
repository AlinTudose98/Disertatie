package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceAdditionalType;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceStatus;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrustServiceHistoryInstance {
    private TrustServiceType serviceType;
    private String serviceName;
    private List<DigitalId> digitalIds;
    private TrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<TrustServiceAdditionalType> additionalTypes;

    public TrustServiceHistoryInstance(TrustService service) {
        this.serviceType = service.getServiceType();
        this.serviceName = service.getServiceName();
        this.digitalIds = service.getDigitalIds();
        this.serviceStatus = service.getServiceStatus();
        this.statusStartingTime = service.getStatusStartingTime();
        this.additionalTypes = service.getAdditionalTypes();
    }
}
