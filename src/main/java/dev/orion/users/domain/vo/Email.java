package dev.orion.users.domain.vo;

import java.util.regex.Pattern;

public class Email {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private String address;
    Pattern ptr = Pattern.compile(EMAIL_REGEX);

    public Email(String address) {
        if (address == null
                || !ptr.matcher(address).matches()) {
            throw new IllegalArgumentException("E-mail invalid!");
        }
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
