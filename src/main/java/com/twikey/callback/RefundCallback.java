package com.twikey.callback;

import org.json.JSONObject;

public interface RefundCallback {

    /**
     * @param refund Json object containing
     * <ul>
     * <li>id: Twikey id</li>
     * <li>iban: IBAN of the beneficiary</li>
     * <li>bic: BIC of the beneficiary</li>
     * <li>amount: Amount of the refund</li>
     * <li>msg: Message for the beneficiary</li>
     * <li>place: Optional place</li>
     * <li>ref: Your reference</li>
     * <li>date: Date when the transfer was requested</li>
     * <li>state: Paid</li>
     * <li>bkdate: Date when the transfer was done</li>
     * </ul>
     */
    void refund(JSONObject refund);
}
