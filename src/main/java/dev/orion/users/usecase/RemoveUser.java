package dev.orion.users.usecase;

import dev.orion.users.infra.repository.UserRepository;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.smallrye.mutiny.Uni;

public class RemoveUser implements UseCase {

    private UserRepository repository = new UserRepositoryImpl();

    @Override
    public Uni<Long> removeUser(String hash) {
        return repository.removeUser(hash);
    }
}
