package it.organigramma.organigramma.model;

import java.time.LocalDate;
import java.util.Objects;

public class RoleAssignment {

    private final Employee employee;
    private final RoleDefinition role;
    private final OrganizationalUnit unit;

    private LocalDate startDate;

    public RoleAssignment(Employee employee,
                          RoleDefinition role,
                          OrganizationalUnit unit) {

        this.employee = Objects.requireNonNull(employee);
        this.role = Objects.requireNonNull(role);
        this.unit = Objects.requireNonNull(unit);
    }

    public Employee getEmployee() {
        return employee;
    }

    public RoleDefinition getRole() {
        return role;
    }

    public OrganizationalUnit getUnit() {
        return unit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }


    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

}
