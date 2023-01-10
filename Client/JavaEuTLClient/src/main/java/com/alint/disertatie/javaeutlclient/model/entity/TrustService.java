package com.alint.disertatie.javaeutlclient.model.entity;

import com.alint.disertatie.javaeutlclient.model.enums.TrustServiceAdditionalType;
import com.alint.disertatie.javaeutlclient.model.enums.TrustServiceStatus;
import com.alint.disertatie.javaeutlclient.model.enums.TrustServiceType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrustService {
    private TrustServiceType serviceType;
    private String serviceName;
    private List<DigitalId> digitalIds;
    private TrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<TrustServiceAdditionalType> additionalTypes;
    private List<TrustServiceHistoryInstance> serviceHistory;
}
