package com.example.blemoduletest.recyclercheckboxdemo;

public class ContactBean {

    private String contactName;

    private String contactEmail;

    private String contactNumber;

    private boolean isChecked;

    private String strChar;

    private int type;

    public ContactBean() {
    }

    public String getStrChar() {
        return strChar;
    }

    public void setStrChar(String strChar) {
        this.strChar = strChar;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
