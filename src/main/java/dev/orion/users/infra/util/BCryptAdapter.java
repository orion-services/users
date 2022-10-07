package dev.orion.users.infra.util;

import dev.orion.users.data.interfaces.Encrypter;
import jodd.crypt.BCrypt;

public class BCryptAdapter implements Encrypter {

    @Override
    public String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    @Override
    public boolean validate(String password) {
        String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt());
        return BCrypt.checkpw(password, passwordHashed);
    }
}