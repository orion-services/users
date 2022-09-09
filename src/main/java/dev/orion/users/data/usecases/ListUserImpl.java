package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.ListUser;
import java.util.List;

public class ListUserImpl implements ListUser {
    private UserRepository repository;

    public ListUserImpl(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * @param params
     * @return
     */
    @Override
    public List<User> list(UserQueryDto query) {
        return repository.findByQuery(query);
    }

    /**
     * @return
     */

}
