package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrustServiceProvider {
    private String name;
    private List<String> tradeNames;
    private String informationUri;
    private List<String> electronicAddresses;
    private List<PostalAddress> postalAddresses;
    private List<TrustService> trustServices;
}
