package com.alint.disertatie.client.eutlwebview.model.message;

import com.alint.disertatie.client.eutlwebview.model.entity.TrustedList;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TlResponse extends ResponseMessage{
    private TrustedList trustedList;

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