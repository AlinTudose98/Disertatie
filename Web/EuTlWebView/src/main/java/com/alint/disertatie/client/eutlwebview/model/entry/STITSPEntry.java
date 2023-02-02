package com.alint.disertatie.client.eutlwebview.model.entry;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class STITSPEntry {
    private int id;
    private boolean activeStatus;
    private String tspName;
    private List<String> quals;
}
