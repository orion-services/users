package dev.orion.users.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group extends PanacheEntityBase {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> users;

}
