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
    protected int status;
    protected String responseType;

    protected String message;

}
