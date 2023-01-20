package com.alint.disertatie.client.eutlwebview.model.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostalAddress {
    private String streetAddress;
    private String locality;
    private String postalCode;
    private String countryName;

    @Override
    public String toString(){
        return streetAddress + ", " + locality + ", " + countryName + ";";
    }
}
