package com.twikey.modal;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public interface DocumentResponse {

    class Document {
        private String mandateNumber;
        private String state;
        private String type;
        private String sequenceType;
        private String signDate;

        private String debtorName;
        private String debtorStreet;
        private String debtorCity;
        private String debtorZip;
        private String debtorCountry;
        private String btwNummer;
        private String countryOfResidence;
        private String debtorEmail;
        private String customerNumber;

        private String iban;
        private String bic;
        private String debtorBank;

        private String contractNumber;

        private final Map<String, String> supplementaryData = new HashMap<>();

        // --- Getters ---
        public String getMandateNumber() {
            return mandateNumber;
        }

        public String getState() {
            return state;
        }

        public String getType() {
            return type;
        }

        public String getSequenceType() {
            return sequenceType;
        }

        public String getSignDate() {
            return signDate;
        }

        public String getDebtorName() {
            return debtorName;
        }

        public String getDebtorStreet() {
            return debtorStreet;
        }

        public String getDebtorCity() {
            return debtorCity;
        }

        public String getDebtorZip() {
            return debtorZip;
        }

        public String getDebtorCountry() {
            return debtorCountry;
        }

        public String getBtwNummer() {
            return btwNummer;
        }

        public String getCountryOfResidence() {
            return countryOfResidence;
        }

        public String getDebtorEmail() {
            return debtorEmail;
        }

        public String getCustomerNumber() {
            return customerNumber;
        }

        public String getIban() {
            return iban;
        }

        public String getBic() {
            return bic;
        }

        public String getDebtorBank() {
            return debtorBank;
        }

        public String getContractNumber() {
            return contractNumber;
        }

        public Map<String, String> getSupplementaryData() {
            return supplementaryData;
        }

        // --- Factory method to build from JSON ---
        public static Document fromJson(JSONObject json, String state) {
            JSONObject mndt = json.getJSONObject("Mndt");

            Document resp = new Document();

            resp.mandateNumber = mndt.getString("MndtId");
            resp.state = state;
            resp.type = mndt.getString("LclInstrm");

            JSONObject ocrncs = mndt.getJSONObject("Ocrncs");
            resp.sequenceType = ocrncs.getString("SeqTp");
            resp.signDate = ocrncs.getJSONObject("Drtn").getString("FrDt");

            JSONObject dbtr = mndt.getJSONObject("Dbtr");
            JSONObject addr = dbtr.getJSONObject("PstlAdr");
            JSONObject ctct = dbtr.getJSONObject("CtctDtls");

            resp.debtorName = dbtr.getString("Nm");
            resp.debtorStreet = addr.optString("AdrLine");
            resp.debtorCity = addr.optString("TwnNm");
            resp.debtorZip = addr.optString("PstCd");
            resp.debtorCountry = addr.optString("Ctry");
            resp.btwNummer = dbtr.optString("Id");
            resp.countryOfResidence = dbtr.optString("CtryOfRes");
            resp.debtorEmail = ctct.optString("EmailAdr");
            resp.customerNumber = ctct.optString("Othr");

            resp.iban = mndt.optString("DbtrAcct");

            JSONObject agent = mndt.getJSONObject("DbtrAgt").getJSONObject("FinInstnId");
            resp.bic = agent.optString("BICFI");
            resp.debtorBank = agent.optString("Nm");

            resp.contractNumber = mndt.getString("RfrdDoc");

            // supplementary data
            for (Object kvItem : mndt.getJSONArray("SplmtryData")) {
                JSONObject item = (JSONObject) kvItem;
                String key = item.getString("Key");
                String value = item.get("Value").toString();
                if (key != null && !key.isEmpty()) {
                    resp.supplementaryData.put(key, value);
                }
            }
            return resp;
        }

        public static List<Document> fromQuery(JSONObject response) {
            JSONArray contracts = response.getJSONArray("Contracts");
            List<Document> docs = new ArrayList<>();
            for (Object kvcontract : contracts) {
                JSONObject contract = (JSONObject) kvcontract;

                Document resp = new Document();
                resp.type = contract.getString("type");
                resp.state = contract.getString("state");
                resp.mandateNumber = contract.getString("mandateNumber");
                resp.contractNumber = contract.getString("contractNumber");
                resp.signDate = contract.getString("signDate");
                resp.iban = contract.getString("iban");
                resp.bic = contract.getString("bic");
                docs.add(resp);
            }
            return docs;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Mandate Number   : ").append(mandateNumber).append("\n");
            sb.append("State            : ").append(state).append("\n");
            sb.append("Type             : ").append(type).append("\n");
            sb.append("Sequence Type    : ").append(sequenceType).append("\n");
            sb.append("Sign Date        : ").append(signDate).append("\n");
            sb.append("Debtor Name      : ").append(debtorName).append("\n");
            sb.append("Debtor Street    : ").append(debtorStreet).append("\n");
            sb.append("Debtor City      : ").append(debtorCity).append("\n");
            sb.append("Debtor Zip       : ").append(debtorZip).append("\n");
            sb.append("Debtor Country   : ").append(debtorCountry).append("\n");
            sb.append("BTW Nummer       : ").append(btwNummer).append("\n");
            sb.append("Country of Res   : ").append(countryOfResidence).append("\n");
            sb.append("Debtor Email     : ").append(debtorEmail).append("\n");
            sb.append("Customer Number  : ").append(customerNumber).append("\n");
            sb.append("IBAN             : ").append(iban).append("\n");
            sb.append("BIC              : ").append(bic).append("\n");
            sb.append("Debtor Bank      : ").append(debtorBank).append("\n");
            sb.append("Contract Number  : ").append(contractNumber).append("\n\n");

            sb.append("Supplementary Data:\n");
            for (Map.Entry<String, String> entry : supplementaryData.entrySet()) {
                sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
            }

            return sb.toString();
        }
    }

    class PdfResponse {
        private final byte[] content;
        private final String contentType;
        private final String filename;

        public PdfResponse(byte[] content, String filename, String contentType) {
            this.content = Objects.requireNonNull(content, "content cannot be null");
            this.filename = filename != null ? filename : "mandate.pdf";
            this.contentType = contentType != null ? contentType : "application/pdf";
        }

        public PdfResponse(byte[] content, String filename) {
            this.content = Objects.requireNonNull(content, "content cannot be null");
            this.filename = filename != null ? filename : "mandate.pdf";
            this.contentType = "application/pdf";
        }

        public byte[] getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }

        public String getFilename() {
            return filename;
        }

        /**
         * Saves the PDF to the specified path.
         * If no path is provided, saves using the filename in the current working directory.
         *
         * @param path optional path to save the file
         * @return the saved file
         * @throws IOException if writing fails
         */
        public Path save(String path) throws IOException {
            String targetPath = path != null ? path : filename;
            Path file = Path.of(targetPath);
            try (OutputStream fos = Files.newOutputStream(file)) {
                fos.write(content);
            }
            return file;
        }

        /**
         * Saves the PDF using the default filename in the current working directory.
         *
         * @return the saved file
         * @throws IOException if writing fails
         */
        public Path save() throws IOException {
            return save(null);
        }

        @Override
        public String toString() {
            return "PdfResponse(filename='" + filename + "', size=" + content.length + " bytes)";
        }
    }

    class MandateCreationResponse {
        private String mandateNumber;
        private String url;
        private String key;

        // --- Getters ---
        public String getMandateNumber() {
            return mandateNumber;
        }

        public String getUrl() {
            return url;
        }

        public String getKey() {
            return key;
        }

        // --- Factory method to build from JSON ---
        public static MandateCreationResponse fromJson(String response) {
            JSONObject json = new JSONObject(new JSONTokener(response));
            MandateCreationResponse resp = new MandateCreationResponse();

            // Handle "MndtId" or "mndtId" (different casing across endpoints)
            if (json.has("MndtId")) {
                resp.mandateNumber = json.getString("MndtId");
            } else if (json.has("mndtId")) {
                resp.mandateNumber = json.getString("mndtId");
            }

            resp.url = json.optString("url", null);
            resp.key = json.optString("key", null); // only present for invite

            return resp;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Mandate Number : ").append(mandateNumber).append("\n");
            sb.append("Url            : ").append(url).append("\n");
            if (key != null) {
                sb.append("Key            : ").append(key).append("\n");
            }
            return sb.toString();
        }
    }

    class CustomerAccessResponse {
        private String token;
        private String url;

        // --- Getters ---
        public String getToken() {
            return token;
        }

        public String getUrl() {
            return url;
        }

        // --- Factory method to build from JSON ---
        public static CustomerAccessResponse fromJson(JSONObject json) {
            CustomerAccessResponse resp = new CustomerAccessResponse();
            resp.token = json.optString("token", null);
            resp.url = json.optString("url", null);
            return resp;
        }

        @Override
        public String toString() {
            String sb = "Token : " + token + "\n" +
                    "Url   : " + url + "\n";
            return sb;
        }
    }
}