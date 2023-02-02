package com.alint.disertatie.engine.javadssengine.message;

import com.alint.disertatie.engine.javadssengine.entity.STISignedFileValidationResult;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STISignedFileValidationResponse extends STIResponseMessage {
    private STISignedFileValidationResult result;
}
