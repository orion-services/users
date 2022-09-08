package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.RemoveUser;

public class RemoveUserImpl implements RemoveUser {

    private UserRepository repository;

    public RemoveUserImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Boolean removeUser(String hash) {
        try {
            return repository.removeUser(hash);
        }catch (Exception e){
            throw e;
        }


    }
}
