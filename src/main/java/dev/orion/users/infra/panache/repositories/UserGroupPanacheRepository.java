package dev.orion.users.infra.panache.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orion.users.data.interfaces.UserGroupRepository;
import dev.orion.users.domain.dto.userGroup.UserGroupQueryDto;
import dev.orion.users.domain.models.UserGroup;
import dev.orion.users.infra.panache.entities.UserGroupPanacheEntity;
import dev.orion.users.infra.panache.entities.UserPanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserGroupPanacheRepository implements UserGroupRepository {
    @Override
    @Transactional
    public UserGroup create(UserGroup userGroup) {
        UserGroupPanacheEntity userGroupPanacheEntity = new UserGroupPanacheEntity();
        userGroupPanacheEntity.userGroupHash = UUID.randomUUID().toString();
        userGroupPanacheEntity.name = userGroup.name;
        userGroupPanacheEntity.usersList = userGroup.users;
        userGroupPanacheEntity.persist();
        return userGroupPanacheEntity.toUserGroup();
    }

    @Override
    @Transactional
    public List<UserGroup> find(UserGroupQueryDto query) {
        PanacheQuery<UserGroupPanacheEntity> userGroupPanache = null;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = mapper.convertValue(query, Map.class);

        if (params.values().stream().allMatch(Objects::isNull)) {
            userGroupPanache = UserGroupPanacheEntity.findAll();
            return userGroupPanache.stream().map(entity -> entity.toUserGroup()).collect(Collectors.toList());
        }
        userGroupPanache = UserGroupPanacheEntity.find(
                "userGroupHash = :hash or name like concat('%',:name,'%')",
                params);
        return userGroupPanache.stream().map(entity -> entity.toUserGroup()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserGroup findById(String id) {
        UserGroupPanacheEntity userGroupPanache = UserGroupPanacheEntity.find("userGroupHash", id).firstResult();
        return userGroupPanache == null ? null : userGroupPanache.toUserGroup();
    }

    @Override
    @Transactional
    public Boolean delete(String id) {
        UserGroupPanacheEntity userGroupPanache = UserGroupPanacheEntity.find("userGroupHash", id).firstResult();
        if (userGroupPanache == null) {
            throw new NotFoundException("UserGroup not found or already removed");
        }
        Boolean userDeleted = userGroupPanache.deleteById(userGroupPanache.id);
        return userDeleted;
    }

    @Override
    public UserGroup update(UserGroup userGroup) {
        UserGroupPanacheEntity userGroupPanache = UserGroupPanacheEntity.find("userGroupHash", userGroup.getUserGroupHash()).firstResult();
        if (userGroupPanache == null) {
            throw new NotFoundException("UserGroup not found or already removed");
        }
//        if(userGroup.getUsers() != null || !userGroup.getUsers().isEmpty()){
//            userGroupPanache.usersList = userGroup.getUsers();
//        }
        if(userGroup.getName() != null || !userGroup.getName().isEmpty()){
            userGroupPanache.name = userGroup.getName();
        }

        return userGroupPanache.toUserGroup();
    }
}
