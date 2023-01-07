package com.alint.disertatie.javaeutlclient.model.entity;

import com.alint.disertatie.javaeutlclient.model.enums.MimeType;
import com.alint.disertatie.javaeutlclient.model.enums.TSLType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtherTSLPointer {
    private TSLType tslType;
    private String tslLocation;
    private String schemeTerritory;
    private String schemeOperatorName;
    private MimeType mimeType;
}
