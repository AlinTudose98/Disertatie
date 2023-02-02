package com.alint.disertatie.server.javaresteutlproviderapi.message;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.STIListOfTrustedLists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STILotlResponse extends STIResponseMessage {
    private STIListOfTrustedLists listOfTrustedLists;
}
