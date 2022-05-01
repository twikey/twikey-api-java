package com.twikey.modal;

import java.util.HashMap;
import java.util.Map;

public class Customer {

    private String lastname;
    private String firstname;
    private String email;
    private String lang;
    private String mobile;
    private String street;
    private String city;
    private String zip;
    private String country;
    private String customerNumber;
    private String companyName;
    private String coc;

    public Customer(){}

    public Customer setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public Customer setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public Customer setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public Customer setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public Customer setStreet(String street) {
        this.street = street;
        return this;
    }

    public Customer setCity(String city) {
        this.city = city;
        return this;
    }

    public Customer setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public Customer setCountry(String country) {
        this.country = country;
        return this;
    }

    public Customer setNumber(String ref) {
        this.customerNumber = ref;
        return this;
    }

    public Customer setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public Customer setCoc(String coc) {
        this.coc = coc;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public String getLang() {
        return lang;
    }

    public String getMobile() {
        return mobile;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getNumber() {
        return customerNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCoc() {
        return coc;
    }

    public Map<String,String> asFormParameters(){
        Map<String,String> params = new HashMap<>();
        params.put("customerNumber", getNumber());
        params.put("email", getEmail());
        params.put("firstname", getFirstname());
        params.put("lastname", getLastname());
        params.put("l", getLang());
        params.put("address", getStreet());
        params.put("city", getCity());
        params.put("zip", getZip());
        params.put("country", getCountry());
        params.put("mobile", getMobile());
        if(getCompanyName() != null){
            params.put("companyName", getCompanyName());
            params.put("coc", getCoc());
        }
        return params;
    }
}
