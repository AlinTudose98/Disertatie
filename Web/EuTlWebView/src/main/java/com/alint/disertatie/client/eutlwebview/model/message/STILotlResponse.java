package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.STIListOfTrustedLists;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STILotlResponse extends STIResponseMessage {
    private STIListOfTrustedLists listOfTrustedLists;
}
