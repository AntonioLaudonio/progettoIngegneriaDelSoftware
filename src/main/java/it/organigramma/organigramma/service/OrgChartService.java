package it.organigramma.organigramma.service;

import it.organigramma.organigramma.repository.RoleAssignmentRepository;

import java.util.Objects;


public class OrgChartService {

    private final RoleAssignmentRepository assignmentRepo;

    public OrgChartService(RoleAssignmentRepository assignmentRepo) {
        this.assignmentRepo = Objects.requireNonNull(assignmentRepo);
    }

}
