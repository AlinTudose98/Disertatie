package com.alint.disertatie.client.eutlwebview.model.entry;

import com.alint.disertatie.client.eutlwebview.model.entity.STITrustServiceHistoryInstance;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STITrustServiceHistoryEntry {
    private int id;
    private STITrustServiceHistoryInstance historyInstance;
}
