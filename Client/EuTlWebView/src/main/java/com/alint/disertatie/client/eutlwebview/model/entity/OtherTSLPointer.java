package com.alint.disertatie.client.eutlwebview.model.entity;

import com.alint.disertatie.client.eutlwebview.model.enums.MimeType;
import com.alint.disertatie.client.eutlwebview.model.enums.TSLType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OtherTSLPointer {
    private TSLType tslType;
    private String tslLocation;
    private String schemeTerritory;
    private String schemeOperatorName;
    private MimeType mimeType;
}
