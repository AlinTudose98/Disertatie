package com.alint.disertatie.client.eutlwebview.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class STICountries {
    private static final Map<String,String> countryMap;

    static {
        countryMap = new HashMap<>();
        countryMap.put("AT","Austria");
        countryMap.put("BE","Belgium");
        countryMap.put("BG","Bulgaria");
        countryMap.put("CY","Cyprus");
        countryMap.put("CZ","Czech Republic");
        countryMap.put("DE","Germany");
        countryMap.put("DK","Denmark");
        countryMap.put("EE","Estonia");
        countryMap.put("EL","Greece");
        countryMap.put("ES","Spain");
        countryMap.put("FI","Finland");
        countryMap.put("FR","France");
        countryMap.put("HR","Croatia");
        countryMap.put("HU","Hungary");
        countryMap.put("IE","Ireland");
        countryMap.put("IS","Iceland");
        countryMap.put("IT","Italy");
        countryMap.put("LI","Liechtenstein");
        countryMap.put("LT","Lithuania");
        countryMap.put("LU","Luxemburg");
        countryMap.put("LV","Latvia");
        countryMap.put("MT","Malta");
        countryMap.put("NL","Netherlands");
        countryMap.put("NO","Norway");
        countryMap.put("PL","Poland");
        countryMap.put("PT","Portugal");
        countryMap.put("RO","Romania");
        countryMap.put("SE","Sweden");
        countryMap.put("SI","Slovenia");
        countryMap.put("SK","Slovakia");
        countryMap.put("EU","European Union");
        countryMap.put("UK", "United Kingdom");
    }

    public static String getCountry(String iso2Code) {
        return countryMap.get(iso2Code);
    }
}
