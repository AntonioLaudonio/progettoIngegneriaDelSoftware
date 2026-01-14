package it.organigramma.organigramma.model;

import java.util.Objects;

public class RoleDefinition {

    private final String id;
    private final String name;

    public RoleDefinition(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleDefinition)) return false;
        RoleDefinition that = (RoleDefinition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
