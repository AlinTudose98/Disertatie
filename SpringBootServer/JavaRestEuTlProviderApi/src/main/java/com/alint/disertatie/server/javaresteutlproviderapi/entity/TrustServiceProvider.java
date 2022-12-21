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
public class TrustServiceProvider {
    private String name;
    private String tradeName;
    private List<String> electronicAddresses;
    private List<PostalAddress> postalAddresses;
    private List<TrustService> trustServices;
}
