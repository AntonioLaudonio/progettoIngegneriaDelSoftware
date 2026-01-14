package it.organigramma.organigramma.repository;

import it.organigramma.organigramma.model.Employee;
import it.organigramma.organigramma.model.OrganizationalUnit;
import it.organigramma.organigramma.model.RoleAssignment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class InMemoryRoleAssignmentRepository implements RoleAssignmentRepository {

    private final List<RoleAssignment> store = new ArrayList<>();

    @Override
    public RoleAssignment save(RoleAssignment assignment) {
        Objects.requireNonNull(assignment);
        store.add(assignment);
        return assignment;
    }

    @Override
    public void delete(RoleAssignment assignment) {
        store.remove(assignment);
    }

    @Override
    public List<RoleAssignment> findByEmployee(Employee employee) {
        return store.stream()
                .filter(a -> a.getEmployee().equals(employee))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<RoleAssignment> findByUnit(OrganizationalUnit unit) {
        return store.stream()
                .filter(a -> a.getUnit().equals(unit))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<RoleAssignment> findAll() {
        return List.copyOf(store);
    }

    @Override
    public void deleteByUnitEmployeeRole(String unitId, String employeeId, String roleId) {
        store.removeIf(a ->
                a.getUnit().getId().equals(unitId) &&
                        a.getEmployee().getId().equals(employeeId) &&
                        a.getRole().getId().equals(roleId)
        );
    }

    @Override
    public void deleteByEmployeeId(String employeeId) {
        store.removeIf(a -> a.getEmployee().getId().equals(employeeId));
    }

}
