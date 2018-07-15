package com.exophrenik.grinia.utilities;

import java.io.Serializable;

public class Profile implements Serializable{

    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String postalCode;
    private String creditCartNumber;
    private String creditExpirationDate;
    private String creditCV;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCreditCartNumber() {
        return creditCartNumber;
    }

    public void setCreditCartNumber(String creditCartNumber) {
        this.creditCartNumber = creditCartNumber;
    }

    public String getCreditExpirationDate() {
        return creditExpirationDate;
    }

    public void setCreditExpirationDate(String creditExpirationDate) {
        this.creditExpirationDate = creditExpirationDate;
    }

    public String getCreditCV() {
        return creditCV;
    }

    public void setCreditCV(String creditCV) {
        this.creditCV = creditCV;
    }
}
