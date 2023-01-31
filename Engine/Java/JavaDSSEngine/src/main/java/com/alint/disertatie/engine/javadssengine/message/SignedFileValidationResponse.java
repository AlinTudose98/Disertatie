package com.alint.disertatie.engine.javadssengine.message;

import com.alint.disertatie.engine.javadssengine.entity.SignedFileValidationResult;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignedFileValidationResponse extends ResponseMessage {
    private SignedFileValidationResult result;
}
