package it.organigramma.organigramma.repository;

import it.organigramma.organigramma.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryEmployeeRepository {

    private final Map<String, Employee> store = new LinkedHashMap<>();

    public Employee save(Employee e) {
        Objects.requireNonNull(e, "employee null");
        store.put(e.getId(), e);
        return e;
    }

    public Optional<Employee> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    public List<Employee> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean existsById(String id) {
        return id != null && store.containsKey(id);
    }

    public void deleteById(String id) {
        if (id != null) store.remove(id);
    }
}
