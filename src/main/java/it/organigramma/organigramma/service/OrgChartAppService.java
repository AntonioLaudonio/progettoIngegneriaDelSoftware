package it.organigramma.organigramma.service;

import it.organigramma.organigramma.dto.*;
import it.organigramma.organigramma.exception.UnitNotDeletableException;
import it.organigramma.organigramma.model.*;
import it.organigramma.organigramma.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrgChartAppService {

    private final InMemoryOrganizationalUnitRepository unitRepo;
    private final InMemoryEmployeeRepository employeeRepo;
    private final InMemoryRoleDefinitionRepository roleRepo;

    private final UnitMoveService unitMoveService;
    private final AllowedRolesService allowedRolesService;
    private final RoleAssignmentService roleAssignmentService;

    public OrgChartAppService(
            InMemoryOrganizationalUnitRepository unitRepo,
            InMemoryEmployeeRepository employeeRepo,
            InMemoryRoleDefinitionRepository roleRepo,
            UnitMoveService unitMoveService,
            AllowedRolesService allowedRolesService,
            RoleAssignmentService roleAssignmentService
    ) {
        this.unitRepo = unitRepo;
        this.employeeRepo = employeeRepo;
        this.roleRepo = roleRepo;
        this.unitMoveService = unitMoveService;
        this.allowedRolesService = allowedRolesService;
        this.roleAssignmentService = roleAssignmentService;
    }

    /*
       ORGCHART TREE
    */

    public UnitNodeDto getOrgChart() {
        List<OrganizationalUnit> roots = unitRepo.findAll().stream()
                .filter(u -> u.getParent() == null)
                .toList();

        if (roots.isEmpty()) return null;

        // una sola root (garantita dalla regola sopra)
        return toDto(roots.get(0));
    }



    private UnitNodeDto toDto(OrganizationalUnit u) {
        return new UnitNodeDto(
                u.getId(),
                u.getName(),
                u.getChildren().stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    /*
       EMPLOYEES
    */

    public List<EmployeeDto> listEmployees() {
        return employeeRepo.findAll().stream()
                .map(e -> new EmployeeDto(e.getId(), e.getName(), e.getSurname()))
                .toList();
    }

    public EmployeeDto createEmployee(CreateEmployeeRequest req) {
        if (req.name == null || req.name.isBlank()) throw new IllegalArgumentException("name obbligatorio");
        if (req.surname == null || req.surname.isBlank()) throw new IllegalArgumentException("surname obbligatorio");

        String id = UUID.randomUUID().toString();
        Employee e = new Employee(id, req.name, req.surname);
        employeeRepo.save(e);

        return new EmployeeDto(e.getId(), e.getName(), e.getSurname());
    }

    public void deleteEmployee(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID obbligatorio");
        }

        if (!employeeRepo.existsById(id)) {
            throw new NoSuchElementException("Dipendente non trovato: " + id);
        }

        // prima rimuovo tutte le assegnazioni di quel dipendente
        roleAssignmentService.deleteByEmployeeId(id);

        employeeRepo.deleteById(id);
    }


    /*
       UNITS
    */

    public void createUnit(CreateUnitRequest req) {
        if (req.name == null || req.name.isBlank()) {
            throw new IllegalArgumentException("name obbligatorio");
        }

        boolean isRootCreation = (req.parentId == null || req.parentId.isBlank());

        // regola: una sola root
        if (isRootCreation) {
            boolean rootAlreadyExists = unitRepo.findAll().stream().anyMatch(u -> u.getParent() == null);
            if (rootAlreadyExists) {
                throw new UnitNotDeletableException("Esiste già una unità radice. Crea sotto-unità dalla radice.");
            }
        }

        String id = UUID.randomUUID().toString();
        OrganizationalUnit unit = new OrganizationalUnit(id, req.name);

        if (!isRootCreation) {
            OrganizationalUnit parent = unitRepo.findById(req.parentId)
                    .orElseThrow(() -> new NoSuchElementException("Parent unit non trovata: " + req.parentId));

            parent.addChild(unit);
            unitRepo.save(unit);
            unitRepo.save(parent);
        } else {
            unitRepo.save(unit);
        }
    }


    public void moveUnit(String unitId, String newParentId) {
        OrganizationalUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + unitId));
        OrganizationalUnit parent = unitRepo.findById(newParentId)
                .orElseThrow(() -> new NoSuchElementException("New parent non trovato: " + newParentId));

        unitMoveService.moveUnit(unit, parent);

        //salva eventuali modifiche
        unitRepo.save(unit);
        unitRepo.save(parent);
    }

    /*
     Regola: elimina SOLO se non ha figli (e non ha assegnazioni).
     */
    public void deleteUnit(String unitId) {
        if (unitId == null || unitId.isBlank()) {
            throw new IllegalArgumentException("unitId obbligatorio");
        }


        OrganizationalUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + unitId));

        boolean hasChildren = unit.getChildren() != null && !unit.getChildren().isEmpty();
        boolean hasAssignments = !roleAssignmentService.getAssignmentsForUnit(unit).isEmpty();

        if (hasChildren || hasAssignments) {
            StringBuilder sb = new StringBuilder();
            sb.append("Impossibile eliminare l'unità '")
                    .append(unit.getName())
                    .append("' (")
                    .append(unit.getId())
                    .append("): ");

            if (hasChildren) sb.append("contiene sotto-unità. ");
            if (hasAssignments) sb.append("contiene assegnazioni. ");

            throw new UnitNotDeletableException(sb.toString().trim());
        }

        // stacca dal padre (così viene tolta dalla lista children del parent)
        unit.detachFromParent();

        // elimina dalla repository
        unitRepo.delete(unit);
    }



    /*
       ASSIGNMENTS
    */

    public void assign(CreateAssignmentRequest req) {
        Employee e = employeeRepo.findById(req.employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee non trovato: " + req.employeeId));
        OrganizationalUnit u = unitRepo.findById(req.unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + req.unitId));
        RoleDefinition r = roleRepo.findById(req.roleId)
                .orElseThrow(() -> new NoSuchElementException("Role non trovato: " + req.roleId));

        roleAssignmentService.assign(e, u, r, req.startDate);
    }

    public RoleDto createRole(CreateRoleRequest req) {
        if (req.name == null || req.name.isBlank()) {
            throw new IllegalArgumentException("name obbligatorio");
        }

        String name = req.name.trim();

        // nome unico (case-insensitive)
        if (roleRepo.existsByNameIgnoreCase(name)) {
            throw new UnitNotDeletableException("Ruolo già esistente: " + name);
        }

        String id = UUID.randomUUID().toString();
        RoleDefinition role = new RoleDefinition(id, name);
        roleRepo.save(role);

        return new RoleDto(role.getId(), role.getName());
    }

    public List<RoleDto> listRoles() {
        return roleRepo.findAll().stream()
                .map(r -> new RoleDto(r.getId(), r.getName()))
                .toList();
    }

    public List<RoleDto> listAllowedRoles(String unitId) {
        OrganizationalUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + unitId));

        return allowedRolesService.getAllowedRoles(unit).stream()
                .map(r -> new RoleDto(r.getId(), r.getName()))
                .toList();
    }

    public void disallowRole(String unitId, String roleId) {
        OrganizationalUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + unitId));
        RoleDefinition role = roleRepo.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role non trovato: " + roleId));

        allowedRolesService.disallowRole(unit, role);

        // safe, anche se in-memory
        unitRepo.save(unit);
    }

    public void allowRole(String unitId, String roleId) {
        OrganizationalUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + unitId));
        RoleDefinition role = roleRepo.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role non trovato: " + roleId));

        allowedRolesService.allowRole(unit, role);

        // safe
        unitRepo.save(unit);
    }

    public List<AssignmentDto> listAssignmentsForUnit(String unitId) {
        OrganizationalUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Unit non trovata: " + unitId));

        return roleAssignmentService.getAssignmentsForUnit(unit).stream()
                .map(a -> new AssignmentDto(
                        unit.getId(),
                        a.getEmployee().getId(),
                        a.getEmployee().getName(),
                        a.getEmployee().getSurname(),
                        a.getRole().getId(),
                        a.getRole().getName(),
                        a.getStartDate()
                ))
                .toList();
    }

    public void deleteAssignment(String unitId, String employeeId, String roleId) {
        roleAssignmentService.deleteByUnitEmployeeRole(unitId, employeeId, roleId);
    }




}
