package com.example.demodssvalidation;

import com.example.demodssvalidation.dss.DSSValidator;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class DemoDssValidationApplicationTests {

    private final ApplicationContext context;

    @Autowired
    public DemoDssValidationApplicationTests(ApplicationContext context) {
        this.context = context;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void validateCertificate() {
        String base64Certificate = "MIIFLTCCBBWgAwIBAgIHIAYFFnADAzANBgkqhkiG9w0BAQUFADA7MQswCQYDVQQGEwJSTzERMA8GA1UEChMIY2VydFNJR04xGTAXBgNVBAsTEGNlcnRTSUdOIFJPT1QgQ0EwHhcNMDYwNzA3MTg0MjE3WhcNMTYwNzA3MTg0MjE3WjBwMQswCQYDVQQGEwJSTzERMA8GA1UEChMIY2VydFNJR04xJjAkBgNVBAsTHWNlcnRTSUdOIFF1YWxpZmllZCBDQSBDbGFzcyAzMSYwJAYDVQQDEx1jZXJ0U0lHTiBRdWFsaWZpZWQgQ0EgQ2xhc3MgMzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAI6X1g0Ra2GpHDXcC17pMRwPduAB+Uz/T5o8hZGCiwgKendbOD5ZTlgwRUZB5IE5nSz6MmnWQEtwIDcrKPyHCSm0byOlx5RSjL+sU2Kz233GjpKjdx88hT2//49xVihqVW4tPq5P0CmX7YpmfU3RjDRdIjfpCj9c07JJIfLG4bxR2xhaPbfkPNqfdf6wkAQur+zVW2DGcUpwl6jfBN9xHqoLASNYT6CqpNkmMfp86ZXEHk7S/ZOrU5tO8P914SGlW9XUs0YHVWi8TcfjrjUTREigVwZ4OeSaH8u5s4k+UhheyNZrewK511UbIwi+0ritwxrUsouwV9T4PfXmCmcTohMCAwEAAaOCAf8wggH7MDMGCCsGAQUFBwEBBCcwJTAjBggrBgEFBQcwAYYXaHR0cDovL29jc3AuY2VydHNpZ24ucm8wDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAYYwaAYDVR0jBGEwX4AU4Iyb2yVJs/F8htayQocL0Gug2eShP6Q9MDsxCzAJBgNVBAYTAlJPMREwDwYDVQQKEwhjZXJ0U0lHTjEZMBcGA1UECxMQY2VydFNJR04gUk9PVCBDQYIGIAYFFnACMB0GA1UdDgQWBBSQg8MtnCgXgwOcPa4Tyncw8hejRjCBhAYDVR0gBH0wezA+BgsrBgEEAYHDOQEBAzAvMC0GCCsGAQUFBwIBFiFodHRwOi8vd3d3LmNlcnRzaWduLnJvL3JlcG9zaXRvcnkwOQYGBACLMAEBMC8wLQYIKwYBBQUHAgEWIWh0dHA6Ly93d3cuY2VydHNpZ24ucm8vcmVwb3NpdG9yeTCBkgYDVR0fBIGKMIGHMIGEoIGBoH+GH2h0dHA6Ly9jcmwuY2VydHNpZ24ucm8vcm9vdC5jcmyGXGxkYXA6Ly9sZGFwLmNlcnRzaWduLnJvL09VPWNlcnRTSUdOIFJPT1QgQ0EsTz1jZXJ0U0lHTixDPVJPP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q7YmluYXJ5MA0GCSqGSIb3DQEBBQUAA4IBAQASFe/7T4ESwPy8F+GxKE81enMBZ1XOXNN+YjyhfO71Dt9A4nlG3FrYxXlKmMMkCl7Y2JW1j+pVmx44E4TfGzKPZTvYq7x93DoWHjiLBmxyYzVhAHvk3dhragLLBW+0G4HeF6XXDgNkJwP4fClBFnPPyQz8e4s6UV7zDZGlfJcdBof1wLAcrBYr0rrauYqCKOw56oqePwDAw1xenWTlMh3Y8xWDVB6iLes5HA5ed8C811IFHy5ms0BhVYbb2mKM1BLqQSAczOxXHv7YPBO+F+6+oI1YfJ2hvEXqAkzlqfxbUh0FqBe+IN4FW+Z+/Q7KSr4rPrAdODVx4BgZ8IfaruYw";

        DSSValidator validator = context.getBean("DSSValidator", DSSValidator.class);

        SimpleCertificateReport report = validator.validateCertificate(base64Certificate);

        System.out.println(report.toString());
    }
}