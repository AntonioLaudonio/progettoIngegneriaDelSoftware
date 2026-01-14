package it.organigramma.organigramma.dto;

public class EmployeeDto {
    public String id;
    public String name;
    public String surname;

    public EmployeeDto(String id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
}
