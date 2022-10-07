package dev.orion.users.data.interfaces;

public interface Encrypter {
    String hash(String plain);

    boolean validate(String password);
}
