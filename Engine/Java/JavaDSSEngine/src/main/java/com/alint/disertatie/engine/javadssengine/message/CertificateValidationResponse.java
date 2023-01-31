package com.alint.disertatie.engine.javadssengine.message;

import com.alint.disertatie.engine.javadssengine.entity.CertificateValidationResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CertificateValidationResponse extends ResponseMessage{
    private List<CertificateValidationResult> certificateChain;

}
