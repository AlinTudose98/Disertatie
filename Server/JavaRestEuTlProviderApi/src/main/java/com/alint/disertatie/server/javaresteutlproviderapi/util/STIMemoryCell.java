package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.STIListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.STIOtherTSLPointer;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.STITrustedList;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.STICountryNotFoundException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.STITrustedListNotParsedException;
import com.google.common.util.concurrent.Monitor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class STIMemoryCell {
    private final Monitor parsingMutex;
    private STIListOfTrustedLists lotl;
    private List<STITrustedList> trustedLists;

    @Autowired
    public STIMemoryCell(@Qualifier("tlParserMutex") Monitor monitor) {
        lotl = new STIListOfTrustedLists();
        trustedLists = new ArrayList<>();
        parsingMutex = monitor;
    }

    public STIListOfTrustedLists getLotl() {
        parsingMutex.enter();
        STIListOfTrustedLists lotl = this.lotl;
        parsingMutex.leave();

        return lotl;
    }

    public void setLotl(STIListOfTrustedLists lotl) {
        parsingMutex.enter();
        this.lotl = lotl;
        parsingMutex.leave();
    }

    public void addTL(STITrustedList tl) {
        parsingMutex.enter();
        String schemeTerritory = tl.getSchemeTerritory();
        for (STITrustedList iter : trustedLists) {
            if(schemeTerritory.equals(iter.getSchemeTerritory())) {
                trustedLists.remove(iter);
                break;
            }
        }
        trustedLists.add(tl);
        parsingMutex.leave();
    }

    public STITrustedList getTL(String countryCode) {
        boolean found = false;
        parsingMutex.enter();
        while(lotl.getPointersToOtherTsl() == null)
        {
            parsingMutex.leave();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                log.warn("Caught InterruptedException... ignoring");
            }
            parsingMutex.enter();
        }
        for (STIOtherTSLPointer iter : lotl.getPointersToOtherTsl()) {
            if(countryCode.equalsIgnoreCase(iter.getSchemeTerritory())) {
                found = true;
                break;
            }
        }
        if(!found) {
            parsingMutex.leave();
            throw new STICountryNotFoundException("Country not found for ISOCODE: " + countryCode);
        }

        for(STITrustedList iter : trustedLists) {
            if(countryCode.equalsIgnoreCase(iter.getSchemeTerritory())) {
                parsingMutex.leave();
                return iter;
            }
        }
        parsingMutex.leave();
        throw new STITrustedListNotParsedException("Trusted List not yet downloaded for country code: " + countryCode);
    }

}
