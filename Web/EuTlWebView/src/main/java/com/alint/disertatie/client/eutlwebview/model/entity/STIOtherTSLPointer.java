package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.STIMimeType;
import com.alint.disertatie.client.eutlwebview.model.enums.STITSLType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STIOtherTSLPointer {
    private STITSLType tslType;
    private String tslLocation;
    private String schemeTerritory;
    private String schemeOperatorName;
    private STIMimeType mimeType;
}
