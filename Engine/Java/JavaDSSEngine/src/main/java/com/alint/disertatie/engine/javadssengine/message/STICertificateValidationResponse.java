package com.alint.disertatie.engine.javadssengine.message;

import com.alint.disertatie.engine.javadssengine.entity.STICertificateValidationResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STICertificateValidationResponse extends STIResponseMessage {
    private List<STICertificateValidationResult> certificateChain;

}
