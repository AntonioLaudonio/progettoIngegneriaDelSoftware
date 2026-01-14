package it.organigramma.organigramma.service;

import it.organigramma.organigramma.model.OrganizationalUnit;

import java.util.Objects;

public class UnitMoveService {

    public void moveUnit(OrganizationalUnit unit, OrganizationalUnit newParent) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(newParent);

        // newParent.addChild(unit) già gestisce detach + controllo cicli
        newParent.addChild(unit);
    }
}
