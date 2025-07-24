package com.twikey;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TwikeyClientTest {

    @Test
    public void verifySignatureAndDecryptAccountInfo() {
        // exiturl defined in template http://example.com?mandatenumber={{mandateNumber}}&status={{status}}&signature={{s}}&account={{account}}
        // outcome http://example.com?mandatenumber=MYDOC&status=ok&signature=8C56F94905BBC9E091CB6C4CEF4182F7E87BD94312D1DD16A61BF7C27C18F569&account=2D4727E936B5353CA89B908309686D74863521CAB32D76E8C2BDD338D3D44BBA

        String outcome = "http://example.com?mandatenumber=MYDOC&status=ok&" +
                "signature=8C56F94905BBC9E091CB6C4CEF4182F7E87BD94312D1DD16A61BF7C27C18F569&" +
                "account=2D4727E936B5353CA89B908309686D74863521CAB32D76E8C2BDD338D3D44BBA";

        String websiteKey = "BE04823F732EDB2B7F82252DDAF6DE787D647B43A66AE97B32773F77CCF12765";
        String doc = "MYDOC";
        String status = "ok";

        String signatureInOutcome = "8C56F94905BBC9E091CB6C4CEF4182F7E87BD94312D1DD16A61BF7C27C18F569";
        String encryptedAccountInOutcome = "2D4727E936B5353CA89B908309686D74863521CAB32D76E8C2BDD338D3D44BBA";
        assertTrue("Valid Signature",TwikeyClient.verifyExiturlSignature(websiteKey,doc,status,null,signatureInOutcome));
        String[] ibanAndBic = TwikeyClient.decryptAccountInformation(websiteKey, doc, encryptedAccountInOutcome);
        assertEquals("BE08001166979213",ibanAndBic[0]);
        assertEquals("GEBABEBB",ibanAndBic[1]);
    }

    @Test
    public void test_getPostDataString_skipEmptyValues() {
        // map with null keys
        Map<String, String> params = new HashMap<>();
        params.put("testing", null);
        params.put("has", "value");

        String data = TwikeyClient.getPostDataString(params);
        assertEquals("has=value", data);
    }

    @Test
    public void test_getPostDataString_urlEncoding() {
        Map<String, String> params = new HashMap<>();
        params.put("safe", "hello world");

        String data = TwikeyClient.getPostDataString(params);
        assertEquals("safe=hello+world", data);
    }

}
