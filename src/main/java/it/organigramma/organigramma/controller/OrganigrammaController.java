package it.organigramma.organigramma.controller;

import it.organigramma.organigramma.dto.*;
import it.organigramma.organigramma.service.OrgChartAppService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrganigrammaController {

    private final OrgChartAppService app;

    public OrganigrammaController(OrgChartAppService app) {
        this.app = app;
    }

    /*
       ORGANIGRAMMA
    */

    @GetMapping("/orgchart")
    public UnitNodeDto getOrgChart() {
        return app.getOrgChart();
    }

    @PostMapping("/units")
    @ResponseStatus(HttpStatus.CREATED)
    public UnitNodeDto createUnit(@RequestBody CreateUnitRequest body) {
        app.createUnit(body);
        return app.getOrgChart();
    }

    @PostMapping("/units/{unitId}/move")
    public UnitNodeDto moveUnit(
            @PathVariable String unitId,
            @RequestBody MoveUnitRequest body
    ) {
        app.moveUnit(unitId, body.newParentId);
        return app.getOrgChart();
    }

    @DeleteMapping("/units/{unitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUnit(@PathVariable String unitId) {
        app.deleteUnit(unitId);
    }

    /*
       RUOLI
    */

    @GetMapping("/roles")
    public List<RoleDto> listRoles() {
        return app.listRoles();
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto createRole(@RequestBody CreateRoleRequest body) {
        return app.createRole(body);
    }

    @GetMapping("/units/{unitId}/allowed-roles")
    public List<RoleDto> listAllowedRoles(@PathVariable String unitId) {
        return app.listAllowedRoles(unitId);
    }

    @PostMapping("/units/{unitId}/allowed-roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void allowRole(@PathVariable String unitId, @PathVariable String roleId) {
        app.allowRole(unitId, roleId);
    }

    @DeleteMapping("/units/{unitId}/allowed-roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disallowRole(@PathVariable String unitId, @PathVariable String roleId) {
        app.disallowRole(unitId, roleId);
    }

    /*
       DIPENDENTI
    */

    @GetMapping("/employees")
    public List<EmployeeDto> listEmployees() {
        return app.listEmployees();
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createEmployee(@RequestBody CreateEmployeeRequest body) {
        return app.createEmployee(body);
    }

    @DeleteMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable String id) {
        app.deleteEmployee(id);
    }

    /*
       ASSEGNAZIONI
    */

    @PostMapping("/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAssignment(@RequestBody CreateAssignmentRequest body) {
        app.assign(body);
    }

    @GetMapping("/units/{unitId}/assignments")
    public List<AssignmentDto> listAssignments(@PathVariable String unitId) {
        return app.listAssignmentsForUnit(unitId);
    }

    @DeleteMapping("/units/{unitId}/assignments/{employeeId}/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(
            @PathVariable String unitId,
            @PathVariable String employeeId,
            @PathVariable String roleId
    ) {
        app.deleteAssignment(unitId, employeeId, roleId);
    }

}
