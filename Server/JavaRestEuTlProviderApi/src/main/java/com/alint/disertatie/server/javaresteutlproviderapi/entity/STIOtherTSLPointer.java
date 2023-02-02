package com.alint.disertatie.server.javaresteutlproviderapi.entity;

import com.alint.disertatie.server.javaresteutlproviderapi.enums.STIMimeType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITSLType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STIOtherTSLPointer {
    private STITSLType tslType;
    private String tslLocation;
    private String schemeTerritory;
    private String schemeOperatorName;
    private STIMimeType mimeType;
}
