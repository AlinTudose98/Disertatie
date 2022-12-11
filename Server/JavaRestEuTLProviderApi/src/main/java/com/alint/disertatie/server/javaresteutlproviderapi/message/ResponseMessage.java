package com.alint.disertatie.server.javaresteutlproviderapi.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {
    private int status;
    private String indication;
    private String authMode;
    private String responseType;
    private String responseBody;

}
