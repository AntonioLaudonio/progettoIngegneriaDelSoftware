package it.organigramma.organigramma.repository;


import it.organigramma.organigramma.model.OrganizationalUnit;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryOrganizationalUnitRepository {

    private final Map<String, OrganizationalUnit> store = new LinkedHashMap<>();

    public OrganizationalUnit save(OrganizationalUnit u) {
        store.put(u.getId(), u);
        return u;
    }

    public Optional<OrganizationalUnit> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<OrganizationalUnit> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(OrganizationalUnit u) {
        store.remove(u.getId());
    }
}
