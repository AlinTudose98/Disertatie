package com.alint.disertatie.server.javaresteutlproviderapi;

import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;


public class JavaRestEutlProvider {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
            JavaRestEutlProvider app = new JavaRestEutlProvider();
            app.verifyLOTL();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void verifyLOTL() throws Exception {

        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();

        TLValidationJob job = new TLValidationJob();
        job.setTrustedListCertificateSource(trustedListsCertificateSource);

    }
}
