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
public class STITrustService {
    private STITrustServiceType serviceType;
    private String serviceName;
    private List<STIDigitalId> digitalIds;
    private STITrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<STITrustServiceAdditionalType> additionalTypes;
    private List<STITrustServiceHistoryInstance> serviceHistory;
}
