package com.alint.disertatie.server.javaresteutlproviderapi.controller;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.STIListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.STITrustedList;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.STIBadRequestException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.STICountryNotFoundException;
import com.alint.disertatie.server.javaresteutlproviderapi.message.STILotlResponse;
import com.alint.disertatie.server.javaresteutlproviderapi.message.STITlResponse;
import com.alint.disertatie.server.javaresteutlproviderapi.util.STIEuTLParser;
import com.alint.disertatie.server.javaresteutlproviderapi.util.STITLParser;
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
public class STIApiController {

    private final ApplicationContext applicationContext;
    private final Environment env;
    private final Monitor parserMutex;
    private final String authMode;
    private final String authForce;
    private final CertificateVerifier certificateVerifier;
    private final TLValidationJob job;

    @Autowired
    public STIApiController(ApplicationContext applicationContext, Environment env, @Qualifier("tlParserMutex") Monitor parserMutex, CertificateVerifier certificateVerifier, TLValidationJob job) {
        this.applicationContext = applicationContext;
        this.env = env;
        this.parserMutex = parserMutex;
        this.certificateVerifier = certificateVerifier;
        this.job = job;
        this.authMode = env.getProperty("java.utils.operation.auth.mode");
        this.authForce = env.getProperty("java.utils.operation.auth.force");
    }

    @RequestMapping(value = "/lotl", produces = MediaType.APPLICATION_JSON_VALUE)
    public STILotlResponse lotl() {
        STILotlResponse response = new STILotlResponse();
        STIListOfTrustedLists lotl =
                this.applicationContext.getBean("STIEuTLParser", STIEuTLParser.class).getListOfTrustedLists();
        response.setListOfTrustedLists(lotl);
        response.setResponseType("LOTL");
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @RequestMapping(value = "/tl/{countryCode}",produces = MediaType.APPLICATION_JSON_VALUE)
    public STITlResponse tl(@PathVariable String countryCode) {
        STITlResponse response = new STITlResponse();
        try {
            STITrustedList tl = this.applicationContext.getBean("STITLParser", STITLParser.class).getTL(countryCode);

            response.setTrustedList(tl);
            response.setResponseType("TL");
            response.setStatus(HttpStatus.OK.value());
        }
        catch (STIBadRequestException e)
        {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
            response.setTrustedList(null);
            response.setResponseType("Error");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        catch (STICountryNotFoundException e)
        {
            log.error(e.getMessage());
            response.setTrustedList(null);
            response.setResponseType("Error");
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        catch (Exception e) {
            log.error("Could not connect to remote host for " + countryCode + " TL.");
            log.error(e.getMessage());
            response.setTrustedList(null);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseType("Error");
        }

        return response;
    }

}