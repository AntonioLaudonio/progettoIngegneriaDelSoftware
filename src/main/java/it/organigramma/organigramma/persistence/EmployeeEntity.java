package it.organigramma.organigramma.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;


    protected EmployeeEntity() {}



    public UUID getId() { return id; }
    public String getName() { return name; }

}
