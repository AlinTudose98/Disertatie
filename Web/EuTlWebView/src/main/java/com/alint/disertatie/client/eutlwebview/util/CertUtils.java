package com.alint.disertatie.client.eutlwebview.util;

import com.alint.disertatie.client.eutlwebview.model.entity.CertificateInfo;
import eu.europa.esig.dss.enumerations.KeyUsageBit;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;

import java.util.ArrayList;
import java.util.Base64;


public class CertUtils {
    public static CertificateInfo getCertificateInfo(String base64Certificate) {
        CertificateToken certificate = DSSUtils.loadCertificateFromBase64EncodedString(base64Certificate);

        CertificateInfo info = new CertificateInfo();
        info.setSerialNumber(String.valueOf(certificate.getSerialNumber()));
        info.setIssuer(certificate.getIssuer().getRFC2253());
        info.setSubject(certificate.getSubject().getPrettyPrintRFC2253());
        info.setValidity(certificate.getNotBefore().toString() + " - " + certificate.getNotAfter().toString());
        info.setKeyUsages(new ArrayList<>());
        for (KeyUsageBit usage : certificate.getKeyUsageBits()) {
            info.getKeyUsages().add(usage.name());
        }
        info.setSigningAlgorithm(certificate.getCertificate().getSigAlgName());

        byte[] data = certificate.getCertificate().getExtensionValue("2.5.29.14");
        byte[] ski = new byte[data.length - 4];
        if (data.length - 4 >= 0)
            System.arraycopy(data, 4, ski, 0, data.length - 4);
        info.setX509SKI(Base64.getEncoder().encodeToString(ski));

        return info;
    }
}
