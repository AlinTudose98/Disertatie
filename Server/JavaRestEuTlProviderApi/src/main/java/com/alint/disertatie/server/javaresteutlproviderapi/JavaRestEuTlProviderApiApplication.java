package com.alint.disertatie.server.javaresteutlproviderapi;

import com.alint.disertatie.server.javaresteutlproviderapi.util.STIEuTLParser;
import com.alint.disertatie.server.javaresteutlproviderapi.util.STIEuTLValidator;
import com.google.common.util.concurrent.Monitor;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.tsl.alerts.LOTLAlert;
import eu.europa.esig.dss.tsl.alerts.TLAlert;
import eu.europa.esig.dss.tsl.alerts.detections.LOTLLocationChangeDetection;
import eu.europa.esig.dss.tsl.alerts.detections.OJUrlChangeDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLExpirationDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLSignatureErrorDetection;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogLOTLLocationChangeAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogOJUrlChangeAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLExpirationAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLSignatureErrorAlertHandler;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.sync.AcceptAllStrategy;
import eu.europa.esig.dss.tsl.sync.ExpirationAndSignatureCheckStrategy;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;

@SpringBootApplication
@Log4j2
public class JavaRestEuTlProviderApiApplication {


    private final Environment env;
    private final ApplicationContext applicationContext;

    @Autowired
    public JavaRestEuTlProviderApiApplication(Environment env, ApplicationContext applicationContext) {
        this.env = env;
        this.applicationContext = applicationContext;
    }

    @Bean
    @Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Monitor tlParserMutex() {
        return new Monitor();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Monitor tlValidationMutex() {
        return new Monitor();
    }

    @Bean
    public CertificateVerifier certificateVerifier() {
        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();

        certificateVerifier.setTrustedCertSources(trustedCertificateSource());
        certificateVerifier.setCrlSource(new OnlineCRLSource());
        certificateVerifier.setOcspSource(new OnlineOCSPSource());
        certificateVerifier.setAIASource(new DefaultAIASource());

        return certificateVerifier;
    }

    @Bean
    public TLValidationJob job() {
        TLValidationJob job = new TLValidationJob();
        job.setOfflineDataLoader(offlineLoader());
        job.setOnlineDataLoader(onlineLoader());
        job.setTrustedListCertificateSource(trustedCertificateSource());
        job.setSynchronizationStrategy(new AcceptAllStrategy());
        job.setCacheCleaner(cacheCleaner());

        LOTLSource europeanLOTL = europeanLOTL();
        job.setListOfTrustedListSources(europeanLOTL);

        job.setLOTLAlerts(Arrays.asList(ojUrlAlert(europeanLOTL), lotlLocationAlert(europeanLOTL)));
        job.setTLAlerts(Arrays.asList(tlSigningAlert(),tlExpirationDetection()));

        return job;
    }

    @Bean
    public CommonTrustedCertificateSource ojJksCertificateSource() throws IOException {
        CommonTrustedCertificateSource source = new CommonTrustedCertificateSource();
        source.importAsTrusted(new KeyStoreCertificateSource(getClass().getClassLoader().getResourceAsStream(env.getProperty("security.keystore.oj-path")), KeyStore.getDefaultType(),"changeit"));

        return source;
    }

    public TrustedListsCertificateSource trustedCertificateSource() {
        return new TrustedListsCertificateSource();
    }

    public LOTLSource europeanLOTL() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(env.getProperty("dss.europa.tl.lotl_url"));
        // lotlSource.setCertificateSource(officialJournalContentKeyStore());
        lotlSource.setCertificateSource( new TrustedListsCertificateSource());

        lotlSource.setSigningCertificatesAnnouncementPredicate(
                new OfficialJournalSchemeInformationURI(
                        Objects.requireNonNull(env.getProperty("dss.europa.oj.ojeu_url"))
                )
        );

        lotlSource.setPivotSupport(true);

        return lotlSource;
    }

    public ExpirationAndSignatureCheckStrategy acceptValidOnlyStrategy() {

        ExpirationAndSignatureCheckStrategy checkStrategy = new ExpirationAndSignatureCheckStrategy();
        checkStrategy.setAcceptExpiredListOfTrustedLists(false);
        checkStrategy.setAcceptExpiredTrustedList(false);
        checkStrategy.setAcceptInvalidListOfTrustedLists(false);
        checkStrategy.setAcceptInvalidTrustedList(false);

        return checkStrategy;
    }

    public DSSFileLoader offlineLoader() {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setCacheExpirationTime(-1); // do not expire cache
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
        return offlineFileLoader;
    }

    public DSSFileLoader onlineLoader() {
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setCacheExpirationTime(0); // do not expire cache
        onlineFileLoader.setDataLoader(dataLoader());
        onlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
        return onlineFileLoader;
    }

    public File tlCacheDirectory() {
        File rootFolder = new File(System.getProperty("java.io.tmpdir"));
        File tslCache = new File(rootFolder, "dss-tsl-loader");
        if (tslCache.mkdirs()) {
            log.info("TL Cache folder : {}", tslCache.getAbsolutePath());
        }
        return tslCache;
    }

    public CommonsDataLoader dataLoader() {
        return new CommonsDataLoader();
    }

    public CacheCleaner cacheCleaner() {
        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.setCleanMemory(true);
        cacheCleaner.setCleanFileSystem(true);
        cacheCleaner.setDSSFileLoader(offlineLoader());

        return cacheCleaner;
    }

    // Alerting

    public TLAlert tlSigningAlert() {
        TLSignatureErrorDetection signatureErrorDetection = new TLSignatureErrorDetection();
        LogTLSignatureErrorAlertHandler handler = new LogTLSignatureErrorAlertHandler();
        return new TLAlert(signatureErrorDetection,handler);
    }

    public TLAlert tlExpirationDetection() {
        TLExpirationDetection expirationDetection = new TLExpirationDetection();
        LogTLExpirationAlertHandler handler = new LogTLExpirationAlertHandler();
        return new TLAlert(expirationDetection, handler);
    }

    public LOTLAlert ojUrlAlert(LOTLSource source) {
        OJUrlChangeDetection ojUrlDetection = new OJUrlChangeDetection(source);
        LogOJUrlChangeAlertHandler handler = new LogOJUrlChangeAlertHandler();
        return new LOTLAlert(ojUrlDetection, handler);
    }

    public LOTLAlert lotlLocationAlert(LOTLSource source) {
        LOTLLocationChangeDetection lotlLocationDetection = new LOTLLocationChangeDetection(source);
        LogLOTLLocationChangeAlertHandler handler = new LogLOTLLocationChangeAlertHandler();
        return new LOTLAlert(lotlLocationDetection, handler);
    }


    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context =
                SpringApplication.run(JavaRestEuTlProviderApiApplication.class, args);

        STIEuTLValidator euTLValidator = context.getBean("STIEuTLValidator", STIEuTLValidator.class);
        STIEuTLParser euTLParser = context.getBean("STIEuTLParser", STIEuTLParser.class);

        Thread verifyThread = new Thread(euTLValidator,"validatorThread");
        Thread parserThread = new Thread(euTLParser,"parserThread");
        verifyThread.start();
        parserThread.start();

    }
}
