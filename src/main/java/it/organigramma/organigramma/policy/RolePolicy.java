package it.organigramma.organigramma.policy;

import it.organigramma.organigramma.model.OrganizationalUnit;
import it.organigramma.organigramma.model.RoleDefinition;

public interface RolePolicy {
    boolean isAllowed(OrganizationalUnit unit, RoleDefinition role);
}
