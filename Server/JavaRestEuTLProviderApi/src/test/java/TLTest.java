import com.alint.disertatie.server.javaresteutlproviderapi.TLValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Test;

public class TLTest {

    @Test
    public void test() throws JsonProcessingException {
        TLValidator tlValidator = new TLValidator();

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        TLValidationJob job = tlValidator.job();
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        job.setTrustedListCertificateSource(trustedListsCertificateSource);
        job.onlineRefresh();
        commonCertificateVerifier.setTrustedCertSources(trustedListsCertificateSource);
        commonCertificateVerifier.setCrlSource(new OnlineCRLSource());
        commonCertificateVerifier.setOcspSource(new OnlineOCSPSource());
        commonCertificateVerifier.setAIASource(new DefaultAIASource());

        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(
                new FileDocument("src/main/resources/tmp/ro.xml"));
        validator.setCertificateVerifier(commonCertificateVerifier);

        System.out.println("------------------------------------------------------------");

        Reports reports = validator.validateDocument();

        SimpleReport simpleReport = reports.getSimpleReport();

        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String xmlResult = mapper.writeValueAsString(simpleReport);

        System.out.println();
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println(xmlResult);
    }
}
