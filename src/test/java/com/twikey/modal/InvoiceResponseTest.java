package com.twikey.modal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

public class InvoiceResponseTest {
    @Test
    public void testIMport() {
        String feed = """
                {
                    "Payments": [
                        {
                            "eventId": "evt_mXvrIbQj14D7Z",
                            "eventType": "payment",
                            "occurredAt": "2026-01-15T15:19:19.471Z",
                            "amount": 30,
                            "currency": "EUR",
                            "origin": {
                                "object": "invoice",
                                "id": "d9e601e7-18ee-4a88-a71c-bc68ace0021e",
                                "number": "192274614",
                                "ref": "truearchitect"
                            },
                            "gateway": {
                                "id": 847,
                                "name": "ABN NL23XXXXXXXXXX8701",
                                "type": "bank",
                                "iban": "NL23ABNA0838498701"
                            },
                            "details": {
                                "source": "direct_debit",
                                "paymentId": 7868235,
                                "transactionE2e": "7864B9BB0E6440159E430875C6E7809D",
                                "mndtId": "Y3QQB4VSUTNQ47R"
                            }
                        },
                        {
                            "eventId": "evt_2wndunjvlJNQP",
                            "eventType": "payment_failure",
                            "occurredAt": "2026-01-15T15:19:21.976Z",
                            "amount": 30,
                            "currency": "EUR",
                            "origin": {
                                "object": "invoice",
                                "id": "d9e601e7-18ee-4a88-a71c-bc68ace0021e",
                                "number": "192274614",
                                "ref": "truearchitect"
                            },
                            "gateway": {
                                "id": 847,
                                "name": "ABN NL23XXXXXXXXXX8701",
                                "type": "bank",
                                "iban": "NL23ABNA0838498701"
                            },
                            "details": {
                                "source": "direct_debit",
                                "paymentId": 7868235,
                                "transactionE2e": "7864B9BB0E6440159E430875C6E7809D",
                                "mndtId": "Y3QQB4VSUTNQ47R"
                            },
                            "error": {
                                "code": "not_routable",
                                "description": "Bank not reachable",
                                "category": "other",
                                "externalCode": "PY01",
                                "action": "send_payment_link",
                                "actionStep": 1
                            }
                        },
                        {
                            "eventId": "evt_vDvDIyeWbPnKr",
                            "eventType": "payment",
                            "occurredAt": "2026-01-15T15:20:06.134Z",
                            "amount": 5,
                            "currency": "EUR",
                            "origin": {
                                "object": "invoice",
                                "id": "929b5c17-c2ef-4027-b2e2-73e900bcd33f",
                                "number": "993109187",
                                "ref": "falsegrow"
                            },
                            "gateway": {
                                "id": 1579,
                                "name": "Mollie",
                                "type": "psp",
                                "iban": null
                            },
                            "details": {
                                "source": "payment_link",
                                "linkId": 812589,
                                "linkMethod": "mastercard"
                            }
                        },
                        {
                            "eventId": "evt_3wJQianYwvJmY",
                            "eventType": "refund",
                            "occurredAt": "2026-01-15T15:20:36.755Z",
                            "amount": 811,
                            "currency": "EUR",
                            "origin": {
                                "object": "invoice",
                                "id": "550d75bb-7cff-4b8d-92f9-d9d56b6daa9d",
                                "number": "634326789",
                                "ref": "trueengineer"
                            },
                            "gateway": {
                                "id": 847,
                                "name": "ABN NL23XXXXXXXXXX8701",
                                "type": "bank",
                                "iban": "NL23ABNA0838498701"
                            },
                            "details": {
                                "source": "credit_transfer",
                                "customerIban": "NL23INGB7520051579",
                                "refundE2e": "B78C1AF520260115152026162308603"
                            }
                        }
                    ]
                }""";
        JSONObject json = new JSONObject(new JSONTokener(feed));
        JSONArray payments = json.getJSONArray("Payments");
        payments.iterator().forEachRemaining(item -> {
            InvoiceResponse.Event.fromJson((JSONObject) item);
        });
    }
}