package com.twikey.modal;

public class Account {

    private final String iban;
    private final String bic;

    /**
     * @param iban Iban part of the account (mandatory)
     * @param bic Bank Identifier, most of the time Twikey will be able to derive the bic from iban, except when specific
     *            branches need to be targeted in which case it's recommended to add the bic.
     */
    public Account(String iban, String bic) {
        this.iban = iban;
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }
}
