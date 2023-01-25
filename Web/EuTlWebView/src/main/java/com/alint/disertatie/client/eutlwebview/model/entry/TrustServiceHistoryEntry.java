package com.alint.disertatie.client.eutlwebview.model.entry;

import com.alint.disertatie.client.eutlwebview.model.entity.TrustServiceHistoryInstance;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrustServiceHistoryEntry {
    private int id;
    private TrustServiceHistoryInstance historyInstance;
}
