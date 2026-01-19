package com.twikey.modal;

import java.util.HashMap;
import java.util.Map;

import static com.twikey.modal.RequestUtils.putIfNotNull;

public interface PaylinkRequests {

    class PaylinkRequest {
        private final long ct;
        private final DocumentRequests.Customer customer;
        private final Map<String, String> linkDetails;

        public PaylinkRequest(long ct, DocumentRequests.Customer customer, Map<String, String> linkDetails) {
            this.ct = ct;
            this.customer = customer;
            this.linkDetails = linkDetails;
        }

        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>(linkDetails);
            params.put("ct", String.valueOf(ct));
            if (customer != null) {
                putIfNotNull(params, "customerNumber", customer.getCustomerNumber());
                putIfNotNull(params, "email", customer.getEmail());
                putIfNotNull(params, "firstname", customer.getFirstname());
                putIfNotNull(params, "lastname", customer.getLastname());
                putIfNotNull(params, "l", customer.getLang());
                putIfNotNull(params, "address", customer.getStreet());
                putIfNotNull(params, "city", customer.getCity());
                putIfNotNull(params, "zip", customer.getZip());
                putIfNotNull(params, "country", customer.getCountry());
                putIfNotNull(params, "mobile", customer.getMobile());
                if (customer.getCompanyName() != null) {
                    putIfNotNull(params, "companyName", customer.getCompanyName());
                    putIfNotNull(params, "coc", customer.getCoc());
                }
            }
            return params;
        }
    }
}
