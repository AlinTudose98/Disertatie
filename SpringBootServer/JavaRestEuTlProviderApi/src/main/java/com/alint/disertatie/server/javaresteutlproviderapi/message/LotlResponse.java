package com.alint.disertatie.server.javaresteutlproviderapi.message;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.ListOfTrustedLists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotlResponse extends ResponseMessage{
    private ListOfTrustedLists listOfTrustedLists;
}
