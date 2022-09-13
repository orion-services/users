package dev.orion.users.infra.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.StatusEnum;
import dev.orion.users.domain.models.User;
import dev.orion.users.infra.entities.UserPanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserPanacheRepository implements UserRepository {
    public UserPanacheRepository() {
    }

    /**
     * @param user
     * @return
     */
    @Override
    @Transactional
    public User create(User user) {
        UserPanacheEntity userPanache = new UserPanacheEntity();
        userPanache.name = user.getName();
        userPanache.hash = user.getHash();
        userPanache.status = user.getStatus();
        userPanache.password = DigestUtils.sha256Hex(user.getPassword());
        userPanache.email = user.getEmail().getAddress();
        userPanache.persist();

        return userPanache.toUser();
    }

    /**
     * @param authDto
     * @return
     */
    @Override
    @Transactional
    public User authenticate(AuthenticateUserDto authDto) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = mapper.convertValue(authDto, Map.class);
        UserPanacheEntity result = UserPanacheEntity.find("email = :email and password = :password",
                params).firstResult();
        return result == null ? null : result.toUser();
    }

    /**
     * @param params
     * @return
     */
    @Override
    @Transactional
    public List<User> findByQuery(UserQueryDto query) {
        PanacheQuery<UserPanacheEntity> userPanache = null;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = mapper.convertValue(query, Map.class);

        if (params.values().stream().allMatch(Objects::isNull)) {
            userPanache = UserPanacheEntity.findAll();
            return userPanache.stream().map(entity -> entity.toUser()).collect(Collectors.toList());
        }
        userPanache = UserPanacheEntity.find(
                "hash = :hash or name like concat('%',:name,'%') or email like concat('%',:email,'%')",
                params);
        return userPanache.stream().map(entity -> entity.toUser()).collect(Collectors.toList());
    }

    /**
     * @param email
     * @return
     */
    @Override
    @Transactional
    public User findByEmail(final String email) {
        UserPanacheEntity userPanache = UserPanacheEntity.find("email", email).firstResult();
        return userPanache == null ? null : userPanache.toUser();
    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Boolean removeUser(String hash) {
        UserPanacheEntity userEntity = UserPanacheEntity.find("hash", hash).firstResult();
        if (userEntity == null) {
            throw new NotFoundException("User not found or already removed");
        }
        Boolean userDeleted = userEntity.deleteById(userEntity.id);
        return userDeleted;
    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional
    public User blockUser(String hash) {
        UserPanacheEntity userEntity = UserPanacheEntity.find("hash", hash).firstResult();
        if (userEntity == null) {
            throw new NotFoundException("User not found or already removed");
        }
        userEntity.status = StatusEnum.BLOCKED.name();
        return userEntity.toUser();
    }
}
