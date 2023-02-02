package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.STISignedFileValidationResult;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STISignedFileValidationResponse extends STIResponseMessage {
    private STISignedFileValidationResult result;
}
