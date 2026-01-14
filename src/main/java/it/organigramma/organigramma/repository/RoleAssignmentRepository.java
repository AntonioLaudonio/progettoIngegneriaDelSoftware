package it.organigramma.organigramma.repository;

import it.organigramma.organigramma.model.Employee;
import it.organigramma.organigramma.model.OrganizationalUnit;
import it.organigramma.organigramma.model.RoleAssignment;

import java.util.List;

public interface RoleAssignmentRepository {
    RoleAssignment save(RoleAssignment assignment);
    void delete(RoleAssignment assignment);

    List<RoleAssignment> findByEmployee(Employee employee);
    List<RoleAssignment> findByUnit(OrganizationalUnit unit);
    List<RoleAssignment> findAll();

    void deleteByUnitEmployeeRole(String unitId, String employeeId, String roleId);
    void deleteByEmployeeId(String employeeId);
}
