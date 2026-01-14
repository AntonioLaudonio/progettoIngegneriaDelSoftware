package it.organigramma.organigramma.service;

import it.organigramma.organigramma.exception.RoleNotAllowedException;
import it.organigramma.organigramma.model.Employee;
import it.organigramma.organigramma.model.OrganizationalUnit;
import it.organigramma.organigramma.model.RoleAssignment;
import it.organigramma.organigramma.model.RoleDefinition;
import it.organigramma.organigramma.policy.RolePolicy;
import it.organigramma.organigramma.repository.RoleAssignmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class RoleAssignmentService {

    private final RoleAssignmentRepository repo;
    private final RolePolicy rolePolicy;

    public RoleAssignmentService(RoleAssignmentRepository repo, RolePolicy rolePolicy) {
        this.repo = Objects.requireNonNull(repo);
        this.rolePolicy = Objects.requireNonNull(rolePolicy);
    }

    public RoleAssignment assign(Employee employee,
                                 OrganizationalUnit unit,
                                 RoleDefinition role,
                                 LocalDate startDate) {

        Objects.requireNonNull(employee);
        Objects.requireNonNull(unit);
        Objects.requireNonNull(role);

        if (!rolePolicy.isAllowed(unit, role)) {
            throw new RoleNotAllowedException(
                    "Ruolo '" + role.getName() + "' non ammesso nell'unità '" + unit.getName() + "'"
            );
        }

        RoleAssignment assignment = new RoleAssignment(employee, role, unit);
        assignment.setStartDate(startDate);

        return repo.save(assignment);
    }


    public List<RoleAssignment> getAssignmentsForUnit(OrganizationalUnit unit) {
        return repo.findByUnit(unit);
    }

    public void deleteByUnitEmployeeRole(String unitId, String employeeId, String roleId) {
        repo.deleteByUnitEmployeeRole(unitId, employeeId, roleId);
    }

    public void deleteByEmployeeId(String employeeId) {
        repo.deleteByEmployeeId(employeeId);
    }

}
