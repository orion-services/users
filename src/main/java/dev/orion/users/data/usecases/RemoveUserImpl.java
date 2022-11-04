package dev.orion.users.data.usecases;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.RemoveUser;

@ApplicationScoped
public class RemoveUserImpl implements RemoveUser {

    @Inject
    protected UserRepository repository;

    @Override
    @Transactional
    public Boolean removeUser(String hash) {
        try {
            return repository.removeUser(hash);
        } catch (Exception exception) {
            throw exception;
        }
    }
}
