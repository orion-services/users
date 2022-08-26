package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import dev.orion.users.infra.repository.UserRepository;
import io.smallrye.mutiny.Uni;

public class RemoveUser implements UseCase {

    private Repository repository = new UserRepository();

    @Override
    public Uni<User> removeUser(String hash) {
        Uni<User> user = repository.find("hash", hash).firstResult();
        repository.delete((User) user);
        return user;
    }
}
