package it.organigramma.organigramma.model;

import java.util.*;

public class OrganizationalUnit {

    private final String id;
    private String name;

    private OrganizationalUnit parent;
    private final List<OrganizationalUnit> children = new ArrayList<>();

    // insieme dei ruoli ammessi per questa unità
    private final Set<RoleDefinition> allowedRoles = new HashSet<>();

    public OrganizationalUnit(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OrganizationalUnit getParent() {
        return parent;
    }

    public List<OrganizationalUnit> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /*
       Composite Pattern (albero)
    */

    public void addChild(OrganizationalUnit child) {
        Objects.requireNonNull(child);
        if (createsCycle(child)) {
            throw new IllegalArgumentException(
                    "Operazione non valida: creerebbe un ciclo nell'organigramma"
            );
        }
        child.detachFromParent();
        child.parent = this;
        children.add(child);
    }

    public void detachFromParent() {
        if (parent != null) {
            parent.children.remove(this);
            parent = null;
        }
    }

    private boolean createsCycle(OrganizationalUnit candidate) {
        OrganizationalUnit current = this;
        while (current != null) {
            if (current == candidate) return true;
            current = current.parent;
        }
        return false;
    }

    /*
       Ruoli ammessi
    */

    public void allowRole(RoleDefinition role) {
        allowedRoles.add(Objects.requireNonNull(role));
    }

    public void disallowRole(RoleDefinition role) {
        allowedRoles.remove(role);
    }

    public boolean isRoleAllowed(RoleDefinition role) {
        return allowedRoles.contains(role);
    }

    public Set<RoleDefinition> getAllowedRoles() {
        return Collections.unmodifiableSet(allowedRoles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganizationalUnit)) return false;
        OrganizationalUnit that = (OrganizationalUnit) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
