package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostalAddress {
    private String streetAddress;
    private String locality;
    private String postalCode;
    private String countryName;
}
