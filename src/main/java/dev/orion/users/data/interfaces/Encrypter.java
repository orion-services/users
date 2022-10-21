package dev.orion.users.data.interfaces;

public interface Encrypter {
    String hash(String password);

    boolean validate(String password);
}
