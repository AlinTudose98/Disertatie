package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.STITrustedList;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STITlResponse extends STIResponseMessage {
    private STITrustedList trustedList;

    @Override
    public String toString() {
        return "TlResponse{" +
                "trustedList=" + trustedList +
                ", status=" + status +
                ", responseType='" + responseType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}