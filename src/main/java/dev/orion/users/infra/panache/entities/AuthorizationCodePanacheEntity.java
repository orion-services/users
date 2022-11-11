package dev.orion.users.infra.panache.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity(name = "authorization_code")
@Table(name = "authorization_code")
@Data
public class AuthorizationCodePanacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "code")
    private String code;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "approved_scopes")
    private String approvedScopes;
    @Column(name = "redirect_uri")
    private String redirectUri;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
}
