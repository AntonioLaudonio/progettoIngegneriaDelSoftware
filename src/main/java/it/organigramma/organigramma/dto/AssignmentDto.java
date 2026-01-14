package it.organigramma.organigramma.dto;

import java.time.LocalDate;

public class AssignmentDto {
    public String unitId;
    public String employeeId;
    public String employeeName;
    public String employeeSurname;
    public String roleId;
    public String roleName;
    public LocalDate startDate;

    public AssignmentDto(
            String unitId,
            String employeeId,
            String employeeName,
            String employeeSurname,
            String roleId,
            String roleName,
            LocalDate startDate
    ) {
        this.unitId = unitId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeSurname = employeeSurname;
        this.roleId = roleId;
        this.roleName = roleName;
        this.startDate = startDate;
    }
}
