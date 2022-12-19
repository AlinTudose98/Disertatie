package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.TrustServiceAdditionalType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TrustServiceStatus;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TrustServiceType;
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
public class TrustServiceHistoryInstance {
    private TrustServiceType serviceType;
    private String serviceName;
    private List<String> digitalIds;
    private TrustServiceStatus serviceStatus;
    private String statusStartingTime;
    private List<TrustServiceAdditionalType> additionalTypes;
}
