package dev.orion.users.infra.panache.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import dev.orion.users.domain.models.Client;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

@Entity(name = "client")
@Table(name = "client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientPanacheEntity extends PanacheEntityBase {
    @Id
    @Column
    public String clientId;
    @Column
    public String name;
    @Column
    public String clientSecret;
    @Column
    public String redirectUri;
    @Column
    public String scope;
    @Column
    public String authorizedGrantTypes;


    public Client toClient(){
        Client client = new Client();
        client.setClientId(this.clientId);
        client.setName(this.name);
        client.setUri(this.redirectUri);
        return client;
    }
}
