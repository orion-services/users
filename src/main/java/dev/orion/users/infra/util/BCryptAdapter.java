package dev.orion.users.infra.util;

import javax.enterprise.context.ApplicationScoped;

import dev.orion.users.data.interfaces.Encrypter;
import io.quarkus.elytron.security.common.BcryptUtil;
import lombok.val;

@ApplicationScoped
public class BCryptAdapter implements Encrypter {

    @Override
    public String hash(String password) {
        return BcryptUtil.bcryptHash(password);
    }

    @Override
    public boolean validate(String password, String passwordHashed) {
        val passwordToBeValidated = BcryptUtil.bcryptHash(password);
        return BcryptUtil.matches(password, passwordHashed);
    }
}