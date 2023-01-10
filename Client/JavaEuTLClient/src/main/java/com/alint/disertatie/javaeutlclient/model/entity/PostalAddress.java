package com.alint.disertatie.javaeutlclient.model.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostalAddress {
    private String streetAddress;
    private String locality;
    private String postalCode;
    private String countryName;
}
