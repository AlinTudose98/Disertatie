package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.ListOfTrustedLists;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LotlResponse extends ResponseMessage{
    private ListOfTrustedLists listOfTrustedLists;
}
