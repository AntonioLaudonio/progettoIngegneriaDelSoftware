package it.organigramma.organigramma.repository;

import it.organigramma.organigramma.model.RoleDefinition;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryRoleDefinitionRepository {

    private final Map<String, RoleDefinition> store = new LinkedHashMap<>();

    public RoleDefinition save(RoleDefinition r) {
        store.put(r.getId(), r);
        return r;
    }

    public Optional<RoleDefinition> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<RoleDefinition> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<RoleDefinition> findByNameIgnoreCase(String name) {
        if (name == null) return Optional.empty();
        String n = name.trim().toLowerCase(Locale.ROOT);

        return store.values().stream()
                .filter(r -> r.getName() != null && r.getName().trim().toLowerCase(Locale.ROOT).equals(n))
                .findFirst();
    }

    public boolean existsByNameIgnoreCase(String name) {
        return findByNameIgnoreCase(name).isPresent();
    }
}
