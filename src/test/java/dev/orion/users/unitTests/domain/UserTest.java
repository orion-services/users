package dev.orion.users.unitTests.domain;

import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.domain.model.Role;
import dev.orion.users.domain.model.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class UserTest {
    private static final String VALID_EMAIL = "orion@test.com";
    private static final String INVALID_EMAIL = "invalid_email";
    private static final String VALID_PASSWORD = "password";
    private static final String VALID_ROLE = "admin";

    private Validator validator = createValidator();

    private Validator createValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Test
    void testValidUser() {
        User user = new User();
        user.setName("JOrion");
        user.setEmail(VALID_EMAIL);
        user.setPassword(VALID_PASSWORD);

        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void testUserInvalidEmail() {
        User user = new User();
        user.setName("Orion");
        user.setEmail(INVALID_EMAIL);
        user.setPassword(VALID_PASSWORD);

        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void testUserMissingName() {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setPassword(VALID_PASSWORD);

        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void testUserMissingEmail() {
        User user = new User();
        user.setName("Orion");
        user.setPassword(VALID_PASSWORD);

        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void testUserMissingPassword() {
        User user = new User();
        user.setName("Orion");
        user.setEmail(VALID_EMAIL);

        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void testAddRole() {
        User user = new User();
        Role role = new Role();
        role.setName(VALID_ROLE);

        user.addRole(role);

        assertTrue(user.getRoleList().contains(VALID_ROLE));
    }

    @Test
    void testGetRoleListEmptyRoles() {
        User user = new User();

        assertTrue(user.getRoleList().contains("user"));
    }

    @Test
    void testGetRoleListNonEmptyRoles() {
        User user = new User();
        Role role1 = new Role();
        role1.setName("role1");
        Role role2 = new Role();
        role2.setName("role2");

        user.addRole(role1);
        user.addRole(role2);

        assertTrue(user.getRoleList().contains("role1"));
        assertTrue(user.getRoleList().contains("role2"));
    }

    @Test
    void testSetEmailValidationCode() {
        User user = new User();

        String oldCode = user.getEmailValidationCode();
        user.setEmailValidationCode();
        String newCode = user.getEmailValidationCode();

        assertNotEquals(oldCode, newCode);
    }

    @Test
    void testRemoveRoles() {
        User user = new User();
        Role role1 = new Role();
        role1.setName("role1");
        Role role2 = new Role();
        role2.setName("role2");

        user.addRole(role1);
        user.addRole(role2);

        user.removeRoles();

        assertTrue(user.getRoles().isEmpty());
    }
}
