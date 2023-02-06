package com.alint.disertatie.engine.javadssengine.dss;

import eu.europa.esig.dss.enumerations.TokenExtractionStrategy;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReport;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateValidator;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import eu.europa.esig.dss.validation.reports.Reports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class STIDSSValidator {
    private final ApplicationContext context;
    private final CertificateVerifier cv;
    private final TLValidationJob job;
    @Autowired
    public STIDSSValidator(ApplicationContext context, CertificateVerifier cv, TLValidationJob job, CommonTrustedCertificateSource jksCertificateSource) {
        this.context = context;
        this.cv = cv;
        this.job = job;

        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        job.setTrustedListCertificateSource(trustedListsCertificateSource);
        job.onlineRefresh();

        cv.addTrustedCertSources(trustedListsCertificateSource);
        cv.addTrustedCertSources(jksCertificateSource);
        cv.setCrlSource(new OnlineCRLSource());
        cv.setOcspSource(new OnlineOCSPSource());
        cv.setAIASource(new DefaultAIASource());
    }

    public SimpleCertificateReport validateCertificate(String base64Certificate, String valTime) throws ParseException {
//        job.onlineRefresh();
        CertificateToken certificate = DSSUtils.loadCertificateFromBase64EncodedString(base64Certificate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date valTimeDate = sdf.parse(valTime);

        CertificateValidator validator = CertificateValidator.fromCertificate(certificate);

        validator.setCertificateVerifier(cv);
        validator.setTokenExtractionStrategy(TokenExtractionStrategy.EXTRACT_CERTIFICATES_AND_REVOCATION_DATA);
        validator.setValidationTime(valTimeDate);
        CertificateReports certificateReports = validator.validate();

        return certificateReports.getSimpleReport();
    }

    public SimpleReport validateSignature(byte[] document) {
//        job.onlineRefresh();
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(new InMemoryDocument(document));

        validator.setCertificateVerifier(cv);
        validator.setTokenExtractionStrategy(TokenExtractionStrategy.EXTRACT_CERTIFICATES_AND_REVOCATION_DATA);

        Reports reports = validator.validateDocument();

        return reports.getSimpleReport();
    }
}
