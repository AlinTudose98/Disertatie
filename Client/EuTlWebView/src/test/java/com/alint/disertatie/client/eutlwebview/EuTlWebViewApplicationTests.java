package com.alint.disertatie.client.eutlwebview;

import com.alint.disertatie.client.eutlwebview.model.entity.ListOfTrustedLists;
import com.alint.disertatie.client.eutlwebview.model.util.ServerRetriever;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootTest
class EuTlWebViewApplicationTests {

    ApplicationContext context;

    @Autowired
    public EuTlWebViewApplicationTests(ApplicationContext context) {
        this.context = context;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void ServerRetrievesWell() throws IOException {
        ServerRetriever serverRetriever = context.getBean("serverRetriever", ServerRetriever.class);

        ListOfTrustedLists lotl = serverRetriever.getListOfTrustedLists();
        System.out.println(lotl);
    }
}
