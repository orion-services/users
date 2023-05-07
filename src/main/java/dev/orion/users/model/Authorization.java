package dev.orion.users.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Authorization {
    @Id
    @GeneratedValue
    private Long id;

    private String token;

    private String refreshToken;
}
