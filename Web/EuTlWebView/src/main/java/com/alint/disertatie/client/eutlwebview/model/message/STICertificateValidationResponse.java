package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.STICertificateValidationResult;
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