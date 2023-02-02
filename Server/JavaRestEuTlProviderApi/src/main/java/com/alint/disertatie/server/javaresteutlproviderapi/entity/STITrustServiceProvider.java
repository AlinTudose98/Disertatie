package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class STITrustServiceProvider {
    private String name;
    private List<String> tradeNames;
    private String informationUri;
    private List<String> electronicAddresses;
    private List<STIPostalAddress> postalAddresses;
    private List<STITrustService> trustServices;
}
