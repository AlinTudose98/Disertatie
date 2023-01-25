package com.alint.disertatie.client.eutlwebview.model.entry;

import com.alint.disertatie.client.eutlwebview.model.entity.TrustService;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrustServiceEntry {
    private int id;
    private TrustService trustService;
    private String qual;
}
