package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.TrustedList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TlResponse extends ResponseMessage{
    private TrustedList trustedList;
}