<p align="center">
  <img src="https://cdn.twikey.com/img/logo.png" height="64"/>
</p>
<h1 align="center">Twikey API client for Java</h1>

Want to allow your customers to pay in the most convenient way, then Twikey is right what you need.

Recurring or occasional payments via (Recurring) Credit Card, SEPA Direct Debit or any other payment method by bringing 
your own payment service provider or by leveraging your bank contract.

Twikey offers a simple and safe multichannel solution to negotiate and collect recurring (or even occasional) payments.
Twikey has integrations with a lot of accounting and CRM packages. It is the first and only provider to operate on a
European level for Direct Debit and can work directly with all major Belgian and Dutch Banks. However you can use the
payment options of your favorite PSP to allow other customers to pay as well.

## Requirements ##

To use the Twikey API client, the following things are required:

+ Get yourself a [Twikey account](https://www.twikey.com).
+ Java >= 8
+ Up-to-date OpenSSL (or other SSL/TLS toolkit)

## Installation ##

The easiest way to install the Twikey API client is 
with [maven](https://maven.apache.org).

    <dependency>
      <groupId>com.twikey</groupId>
      <artifactId>twikey-api-java</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>

## How to create anything ##

The api works the same way regardless if you want to create a mandate, a transaction, an invoice or even a paylink.
the following steps should be implemented:

1. Use the Twikey API client to create or import your item.

2. Once available, our platform will send an asynchronous request to the configured webhook
   to allow the details to be retrieved. As there may be multiple items ready for you a "feed" endpoint is provided
   which acts like a queue that can be read until empty till the next time.

3. The customer returns, and should be satisfied to see that the action he took is completed.

Find our full documentation online on [api.twikey.com](https://api.twikey.com).

## Getting started ##

Initializing the Twikey API client using the Requests library. 
and configure your API key which you can find in the [Twikey merchant interface](https://www.twikey.com).

```java
TwikeyClient twikeyClient = new TwikeyClient(apiKey)
        .withUserAgent("myApp");
``` 

## Documents

Invite a customer to sign a SEPA mandate using a specific behaviour template (ct) that allows you to configure 
the behaviour or flow that the customer will experience. This 'ct' can be found in the template section of the settings.

```java

Customer customer = new Customer()
   .setNumber("customerNum123")
   .setEmail("no-reply@example.com")
   .setFirstname("Twikey")
   .setLastname("Support")
   .setStreet("Derbystraat 43")
   .setCity("Gent")
   .setZip("9000")
   .setCountry("BE")
   .setLang("nl")
   .setMobile("32498665995");

Map<String,String> extraParams = null;

JSONObject invite = api.document().create(ct,customer,extraParams);

```

_After creation, the link available in invite['url'] can be used to redirect the customer into the signing flow or even 
send him a link through any other mechanism. Ideally you store the mandatenumber for future usage (eg. sending transactions)._


### Feed

Once signed, a webhook is sent (see below) after which you can fetch the detail through the document feed, which you can actually
think of as reading out a queue. Since it'll return you the changes since the last time you called it.

```java
twikeyClient.document().feed(new DocumentCallback() {
   @Override
   public void newDocument(JSONObject newMandate) {
        System.out.println("New mandate: "+newMandate);
   }
   
   @Override
   public void updatedDocument(JSONObject updatedMandate) {
        System.out.println("Updated mandate: "+updatedMandate);
   }
   
   @Override
   public void cancelledDocument(JSONObject cancelledMandate) {
        System.out.println("Cancelled mandate: "+cancelledMandate);
   }
})
```

## Transactions

Send new transactions and act upon feedback from the bank.

```java

tx = twikeyClient.transaction().create("CORERECURRENTNL16318",Map.of(
   "message" , "Test Message",
   "ref" , "Merchant Reference",
   "amount" , 10.00, 
   "place" , "Here"
));
```

### Feed

```java
twikeyClient.transaction().feed(tx -> System.out.println("Updated transaction: "+tx))
```

## Webhook ##

When wants to inform you about new updates about documents or payments a `webhookUrl` specified in your api settings be called.  

```java
String incomingSignature = request.getHeader("X-SIGNATURE");
String payload = request.getQueryString();
boolean valid = twikeyClient.verifyWebHookSignature(incomingSignature,payload);
```

## API documentation ##

If you wish to learn more about our API, please visit the [Twikey Api Page](https://api.twikey.com).
API Documentation is available in English.

## Want to help us make our API client even better? ##

Want to help us make our API client even better? We
take [pull requests](https://github.com/twikey/twikey-api-python/pulls). 

## Support ##

Contact: [www.twikey.com](https://www.twikey.com)
