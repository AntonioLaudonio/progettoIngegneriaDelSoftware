package it.organigramma.organigramma.dto;

import java.time.LocalDate;

public class CreateAssignmentRequest {
    public String employeeId;
    public String unitId;
    public String roleId;
    public LocalDate startDate;
}
