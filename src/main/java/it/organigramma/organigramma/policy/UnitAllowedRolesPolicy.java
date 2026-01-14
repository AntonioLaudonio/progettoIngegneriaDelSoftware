package it.organigramma.organigramma.policy;

import it.organigramma.organigramma.model.OrganizationalUnit;
import it.organigramma.organigramma.model.RoleDefinition;


import org.springframework.stereotype.Component;

@Component
public class UnitAllowedRolesPolicy implements RolePolicy {
    @Override
    public boolean isAllowed(OrganizationalUnit unit, RoleDefinition role) {
        return unit.isRoleAllowed(role);
    }
}
