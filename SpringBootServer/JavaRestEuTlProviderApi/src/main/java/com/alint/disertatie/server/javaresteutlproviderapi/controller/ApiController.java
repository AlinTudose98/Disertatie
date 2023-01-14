package com.alint.disertatie.server.javaresteutlproviderapi.controller;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.ListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.TrustedList;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.BadRequestException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.CountryNotFoundException;
import com.alint.disertatie.server.javaresteutlproviderapi.message.LotlResponse;
import com.alint.disertatie.server.javaresteutlproviderapi.message.TlResponse;
import com.alint.disertatie.server.javaresteutlproviderapi.util.EuTLParser;
import com.alint.disertatie.server.javaresteutlproviderapi.util.TLParser;
import com.google.common.util.concurrent.Monitor;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/api")
public class ApiController {

    private final ApplicationContext applicationContext;
    private final Environment env;
    private final Monitor parserMutex;
    private final String authMode;
    private final String authForce;
    private final CertificateVerifier certificateVerifier;
    private final TLValidationJob job;

    @Autowired
    public ApiController(ApplicationContext applicationContext, Environment env, @Qualifier("tlParserMutex") Monitor parserMutex, CertificateVerifier certificateVerifier, TLValidationJob job) {
        this.applicationContext = applicationContext;
        this.env = env;
        this.parserMutex = parserMutex;
        this.certificateVerifier = certificateVerifier;
        this.job = job;
        this.authMode = env.getProperty("java.utils.operation.auth.mode");
        this.authForce = env.getProperty("java.utils.operation.auth.force");
    }

    @RequestMapping(value = "/lotl", produces = MediaType.APPLICATION_JSON_VALUE)
    public LotlResponse lotl() {
        LotlResponse response = new LotlResponse();
        ListOfTrustedLists lotl =
                this.applicationContext.getBean("euTLParser", EuTLParser.class).getListOfTrustedLists();
        response.setListOfTrustedLists(lotl);
        response.setResponseType("LOTL");
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @RequestMapping(value = "/tl/{countryCode}",produces = MediaType.APPLICATION_JSON_VALUE)
    public TlResponse tl(@PathVariable String countryCode) {
        TlResponse response = new TlResponse();
        try {
            TrustedList tl = this.applicationContext.getBean("TLParser", TLParser.class).getTL(countryCode);

            response.setTrustedList(tl);
            response.setResponseType("TL");
            response.setStatus(HttpStatus.OK.value());
        }
        catch (BadRequestException e)
        {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
            response.setTrustedList(null);
            response.setResponseType("Error");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        catch (CountryNotFoundException e)
        {
            log.error(e.getMessage());
            response.setTrustedList(null);
            response.setResponseType("Error");
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }

        return response;
    }

}