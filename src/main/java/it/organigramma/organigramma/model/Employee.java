package it.organigramma.organigramma.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Employee {

    private final String id;
    private String name;
    private String surname;

    private final List<RoleAssignment> assignments = new ArrayList<>();

    public Employee(String id, String name, String surname) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.surname = Objects.requireNonNull(surname);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<RoleAssignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id.equals(employee.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
