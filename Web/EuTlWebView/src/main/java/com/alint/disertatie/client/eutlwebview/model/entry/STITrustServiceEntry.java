package com.alint.disertatie.client.eutlwebview.model.entry;

import com.alint.disertatie.client.eutlwebview.model.entity.STITrustService;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STITrustServiceEntry {
    private int id;
    private STITrustService trustService;
    private String qual;
}
