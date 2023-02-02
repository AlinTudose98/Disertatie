package com.alint.disertatie.client.eutlwebview.model.message;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STIResponseMessage {
    protected int status;
    protected String responseType;

    protected String message;

}
