package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.ListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.OtherTSLPointer;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.TrustedList;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.CountryNotFoundException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.TrustedListNotParsedException;
import com.google.common.util.concurrent.Monitor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class MemoryCell {
    private final Monitor parsingMutex;
    private ListOfTrustedLists lotl;
    private List<TrustedList> trustedLists;

    @Autowired
    public MemoryCell(@Qualifier("tlParserMutex") Monitor monitor) {
        lotl = new ListOfTrustedLists();
        trustedLists = new ArrayList<>();
        parsingMutex = monitor;
    }

    public ListOfTrustedLists getLotl() {
        parsingMutex.enter();
        ListOfTrustedLists lotl = this.lotl;
        parsingMutex.leave();

        return lotl;
    }

    public void setLotl(ListOfTrustedLists lotl) {
        parsingMutex.enter();
        this.lotl = lotl;
        parsingMutex.leave();
    }

    public void addTL(TrustedList tl) {
        parsingMutex.enter();
        String schemeTerritory = tl.getSchemeTerritory();
        for (TrustedList iter : trustedLists) {
            if(schemeTerritory.equals(iter.getSchemeTerritory())) {
                trustedLists.remove(iter);
                break;
            }
        }
        trustedLists.add(tl);
        parsingMutex.leave();
    }

    public TrustedList getTL(String countryCode) {
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
        for (OtherTSLPointer iter : lotl.getPointersToOtherTsl()) {
            if(countryCode.equals(iter.getSchemeTerritory())) {
                found = true;
                break;
            }
        }
        if(!found) {
            parsingMutex.leave();
            throw new CountryNotFoundException("Country not found for ISOCODE: " + countryCode);
        }

        for(TrustedList iter : trustedLists) {
            if(countryCode.equals(iter.getSchemeTerritory())) {
                parsingMutex.leave();
                return iter;
            }
        }
        parsingMutex.leave();
        throw new TrustedListNotParsedException("Trusted List not yet downloaded for country code: " + countryCode);
    }

}
