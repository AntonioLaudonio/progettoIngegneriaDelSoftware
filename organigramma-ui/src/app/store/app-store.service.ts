import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, map, tap } from 'rxjs';
import { OrgchartService, UnitNodeDto } from '../services/orgchart.service';
import { EmployeeService, EmployeeDto } from '../services/employee.service';
import { RolesService, RoleDto } from '../services/roles.service';
import { AssignmentsService, AssignmentDto } from '../services/assignments.service';

@Injectable({ providedIn: 'root' })
export class AppStoreService {
  // ORGCHART
  private readonly orgChartSubject = new BehaviorSubject<UnitNodeDto | null>(null);
  readonly orgChart$ = this.orgChartSubject.asObservable();

  private readonly selectedUnitIdSubject = new BehaviorSubject<string | null>(null);
  readonly selectedUnitId$ = this.selectedUnitIdSubject.asObservable();

  private readonly selectedEmployeeIdSubject = new BehaviorSubject<string | null>(null);
  readonly selectedEmployeeId$ = this.selectedEmployeeIdSubject.asObservable();

  private readonly highlightUnitIdSubject = new BehaviorSubject<string | null>(null);
  readonly highlightUnitId$ = this.highlightUnitIdSubject.asObservable();

  // EMPLOYEES
  private readonly employeesSubject = new BehaviorSubject<EmployeeDto[]>([]);
  readonly employees$ = this.employeesSubject.asObservable();

  // ROLES (catalogo + allowed per unità)
  private readonly rolesSubject = new BehaviorSubject<RoleDto[]>([]);
  readonly roles$ = this.rolesSubject.asObservable();

  private readonly allowedRolesSubject = new BehaviorSubject<RoleDto[]>([]);
  readonly allowedRoles$ = this.allowedRolesSubject.asObservable();

  // ASSIGNMENTS (per unità selezionata)
  private readonly assignmentsSubject = new BehaviorSubject<AssignmentDto[]>([]);
  readonly assignments$ = this.assignmentsSubject.asObservable();

  // nodo selezionato (UnitNodeDto) derivato da tree + selectedUnitId
  readonly selectedUnit$ = combineLatest([this.orgChart$, this.selectedUnitId$]).pipe(
    map(([tree, id]) => (tree && id) ? this.findNodeById(tree, id) : null)
  );

  constructor(
    private orgchartApi: OrgchartService,
    private employeeApi: EmployeeService,
    private rolesApi: RolesService,
    private assignmentsApi: AssignmentsService
  ) {}

  // INIT
  loadInitial() {
    this.refreshOrgChart();
    this.refreshEmployees();
    this.refreshRoles();
  }

  // ORGCHART ACTIONS
  refreshOrgChart() {
    this.orgchartApi.getOrgchart().subscribe(tree => {
      this.orgChartSubject.next(tree);

      // se non c'è tree, reset selezione
      if (!tree) {
        this.selectedUnitIdSubject.next(null);
        return;
      }

      // se la selezione non esiste più, la resettiamo
      const sel = this.selectedUnitIdSubject.value;
      if (sel && !this.findNodeById(tree, sel)) {
        this.selectedUnitIdSubject.next(null);
      }
    });
  }

  selectUnit(unitId: string) {
    this.selectedEmployeeIdSubject.next(null);
    this.selectedUnitIdSubject.next(unitId);
  }

  createUnit(body: { name: string; parentId?: string | null }) {
    this.orgchartApi.createUnit(body).subscribe(() => {
      this.refreshOrgChart();
    });
  }

  deleteUnit(unitId: string) {
    // ritorniamo l'Observable così il componente può mostrare errori (409 ecc.)
    return this.orgchartApi.deleteUnit(unitId);
  }

  // EMPLOYEE ACTIONS
  refreshEmployees() {
    this.employeeApi.list().subscribe(list => {
      this.employeesSubject.next(list);
    });
  }

  createEmployee(body: { name: string; surname: string }) {
    this.employeeApi.create(body).subscribe(() => {
      this.refreshEmployees();
    });
  }

  deleteEmployee(id: string) {
    // backend: se hai fatto cascade, rimuove anche le assegnazioni
    this.employeeApi.delete(id).subscribe(() => this.refreshEmployees());
  }

  // ROLES ACTIONS
  refreshRoles() {
    this.rolesApi.listRoles().subscribe(list => {
      this.rolesSubject.next(list);
    });
  }

  refreshAllowedRoles(unitId: string) {
    this.rolesApi.listAllowedRoles(unitId).subscribe(list => {
      this.allowedRolesSubject.next(list);
    });
  }

  createRole(name: string) {
    return this.rolesApi.createRole({ name }).pipe(
      tap(() => this.refreshRoles())
    );
  }

  allowRole(unitId: string, roleId: string) {
    return this.rolesApi.allowRole(unitId, roleId).pipe(
      tap(() => this.refreshAllowedRoles(unitId))
    );
  }

  disallowRole(unitId: string, roleId: string) {
    return this.rolesApi.disallowRole(unitId, roleId).pipe(
      tap(() => this.refreshAllowedRoles(unitId))
    );
  }

  // ASSIGNMENTS ACTIONS
  refreshAssignments(unitId: string) {
    this.assignmentsApi.listByUnit(unitId).subscribe((list: AssignmentDto[]) => {
      this.assignmentsSubject.next(list);
    });
  }

  createAssignment(body: { employeeId: string; unitId: string; roleId: string; startDate?: string | null }) {
    return this.assignmentsApi.create(body).pipe(
      tap(() => this.refreshAssignments(body.unitId))
    );
  }

  deleteAssignment(unitId: string, employeeId: string, roleId: string) {
    return this.assignmentsApi.delete(unitId, employeeId, roleId).pipe(
      tap(() => this.refreshAssignments(unitId))
    );
  }

  // helper
  private findNodeById(node: UnitNodeDto, id: string): UnitNodeDto | null {
    if (node.id === id) return node;
    for (const c of (node.children ?? [])) {
      const r = this.findNodeById(c, id);
      if (r) return r;
    }
    return null;
  }

  get selectedUnitIdValue(): string | null {
    return this.selectedUnitIdSubject.value;
  }

}
