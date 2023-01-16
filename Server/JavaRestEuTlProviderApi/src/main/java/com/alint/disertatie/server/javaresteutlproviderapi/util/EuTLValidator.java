package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.google.common.util.concurrent.Monitor;

import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.spi.tsl.TLValidationJobSummary;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;

import lombok.Getter;
import lombok.Setter;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter
@Setter
@Log4j2
public class EuTLValidator implements Runnable{

    private boolean doStop = false;

    private long runInterval;
    private long lastVerifyTimestamp;
    private final Environment env;

    private final Monitor mutex;

    private final TLValidationJob job;
    private final CertificateVerifier certificateVerifier;

    @Autowired
    public EuTLValidator(Environment env, @Qualifier("tlValidationMutex") Monitor mutex, TLValidationJob job, CertificateVerifier certificateVerifier) {
        this.env = env;
        this.mutex = mutex;
        this.job = job;
        this.certificateVerifier = certificateVerifier;
        this.lastVerifyTimestamp = 0;
    }

    @PostConstruct
    private void configure(){
        this.runInterval = Long.parseLong(
                env.getProperty("dss.europa.tl.validation.run_interval_s","600"));

        TrustedListsCertificateSource certificateSource = new TrustedListsCertificateSource();

        this.job.setTrustedListCertificateSource(certificateSource);
        this.certificateVerifier.setTrustedCertSources(certificateSource);
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    public synchronized boolean keepRunning() {
        return !this.doStop;
    }

    @SneakyThrows
    @Override
    public void run() {
        while(keepRunning()) {
            if(System.currentTimeMillis() - this.lastVerifyTimestamp > runInterval * 1000) {
                try {
                    mutex.enter();
                    log.info("Starting online refresh");
                    job.onlineRefresh();
                    this.lastVerifyTimestamp = System.currentTimeMillis();

                    TLValidationJobSummary summary = job.getSummary();
                    Indication indication = summary.getLOTLInfos().get(0).getValidationCacheInfo().getIndication();
                    SubIndication subIndication = summary.getLOTLInfos().get(0).getValidationCacheInfo().getSubIndication();

                    if(!indication.name().equals(Indication.TOTAL_PASSED.name())) {
                        log.warn("Validation finished:\n>  Indication: " + indication.name()
                        + "\n>>    Subindication: " + subIndication.name());
                    }
                    else
                        log.info("Validation finished: TOTAL_PASSED");

                }
                finally {
                    mutex.leave();
                }
            }
        }
    }
}
