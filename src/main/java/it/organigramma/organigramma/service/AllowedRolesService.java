package it.organigramma.organigramma.service;

import it.organigramma.organigramma.model.OrganizationalUnit;
import it.organigramma.organigramma.model.RoleDefinition;

import java.util.Objects;
import java.util.Set;

public class AllowedRolesService {

    public void allowRole(OrganizationalUnit unit, RoleDefinition role) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(role);
        unit.allowRole(role);
    }

    public void disallowRole(OrganizationalUnit unit, RoleDefinition role) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(role);
        unit.disallowRole(role);
    }

    public Set<RoleDefinition> getAllowedRoles(OrganizationalUnit unit) {
        Objects.requireNonNull(unit);
        return unit.getAllowedRoles();
    }
}
