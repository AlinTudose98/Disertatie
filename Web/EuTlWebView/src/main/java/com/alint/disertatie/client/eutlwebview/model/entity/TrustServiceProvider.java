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
    private String tradeName;
    private List<String> electronicAddresses;
    private List<PostalAddress> postalAddresses;
    private List<TrustService> trustServices;
}
