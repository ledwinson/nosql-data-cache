package le.cache.bis.services.impl.data;

import java.util.Date;

public class Employee {

    private String title;
    private String suffix;
    private String firstName;
    private String lastName;
    private String upperLastName;
    private String otherGivenName;
    private String sex;
    private Date dob;
    private String temporaryFlag;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUpperLastName() {
        return upperLastName;
    }

    public void setUpperLastName(String upperLastName) {
        this.upperLastName = upperLastName;
    }

    public String getOtherGivenName() {
        return otherGivenName;
    }

    public void setOtherGivenName(String otherGivenName) {
        this.otherGivenName = otherGivenName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getTemporaryFlag() {
        return temporaryFlag;
    }

    public void setTemporaryFlag(String temporaryFlag) {
        this.temporaryFlag = temporaryFlag;
    }
}
