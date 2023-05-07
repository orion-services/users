package dev.orion.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ACTIVATED("ACTIVATED"),
    DISABLED("DISABLED");

    private String description;
}
