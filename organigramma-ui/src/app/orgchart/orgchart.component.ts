import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppStoreService } from '../store/app-store.service';
import type { UnitNodeDto } from '../services/orgchart.service';
import type { RoleDto } from '../services/roles.service';
import { BehaviorSubject, combineLatest, map, startWith } from 'rxjs';

@Component({
  selector: 'app-orgchart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orgchart.component.html',
  styleUrls: ['./orgchart.component.css']
})
export class OrgchartComponent implements OnInit {

  private readonly store = inject(AppStoreService);
  private readonly destroyRef = inject(DestroyRef);

  readonly tree$ = this.store.orgChart$;
  readonly employees$ = this.store.employees$;
  readonly selectedUnit$ = this.store.selectedUnit$;
  readonly selectedUnitId$ = this.store.selectedUnitId$;

  // roles
  readonly roles$ = this.store.roles$;
  readonly allowedRoles$ = this.store.allowedRoles$;

  // assignments
  readonly assignments$ = this.store.assignments$;

  // UI state
  search = '';
  private readonly search$ = new BehaviorSubject<string>('');

  // lista dipendenti filtrata dal search
  readonly filteredEmployees$ = combineLatest([
    this.employees$,
    this.search$.pipe(startWith(''))
  ]).pipe(
    map(([employees, q]) => {
      const query = (q ?? '').trim().toLowerCase();
      if (!query) return employees;

      return (employees ?? []).filter(e => {
        const fullName = `${e?.name ?? ''} ${e?.surname ?? ''}`.toLowerCase();
        const id = `${e?.id ?? ''}`.toLowerCase();
        return fullName.includes(query) || id.includes(query);
      });
    })
  );

  onSearchChange(value: string): void {
    this.search$.next(value ?? '');
  }

  // MODAL "NUOVO DIPENDENTE"
  isCreateEmployeeOpen = false;
  empForm = { name: '', surname: '' };
  empError = '';

  // MODAL "NUOVA UNITÀ"
  isCreateUnitOpen = false;
  unitForm = { name: '', parentId: null as string | null };
  formError = '';

  // ERRORI ELIMINAZIONE UNITÀ
  unitDeleteError = '';

  // MODAL "RUOLI AMMESSI"
  isAllowedRolesOpen = false;
  rolesSearch = '';
  newRoleName = '';
  rolesModalError = '';
  private rolesUnitId: string | null = null;
  allowedRoleIds = new Set<string>();

  // MODAL "ASSEGNA DIPENDENTE"
  isAssignOpen = false;
  assignError = '';
  assignForm = {
    employeeId: '' as string,
    roleId: '' as string,
    startDate: '' as string // YYYY-MM-DD
  };

  // dipendente selezionato nel modal "Assegna"
  private readonly selectedAssignEmployeeId$ = new BehaviorSubject<string>('');

  // ruoli disponibili (ammessi - già assegnati a quel dipendente in questa unità)
  readonly availableRolesForSelectedEmployee$ = combineLatest([
    this.allowedRoles$,
    this.assignments$.pipe(startWith([] as any[])),
    this.selectedAssignEmployeeId$.pipe(startWith(''))
  ]).pipe(
    map(([allowedRoles, assignments, employeeId]) => {
      const empId = (employeeId ?? '').toString();
      if (!empId) return allowedRoles;

      const alreadyRoleIds = new Set(
        (assignments ?? [])
          .filter(a => `${a?.employeeId ?? ''}` === empId)
          .map(a => `${a?.roleId ?? ''}`)
      );

      return (allowedRoles ?? []).filter(r => !alreadyRoleIds.has(`${r?.id ?? ''}`));
    })
  );

  onAssignEmployeeChange(employeeId: string): void {
    this.assignForm.employeeId = employeeId ?? '';
    this.assignForm.roleId = ''; // reset ruolo al cambio dipendente
    this.selectedAssignEmployeeId$.next(this.assignForm.employeeId);
  }

  ngOnInit(): void {
    this.store.loadInitial();
    this.search$.next(this.search);
  }

  // SELEZIONE
  onSelectUnit(node: UnitNodeDto): void {
    this.unitDeleteError = '';
    this.store.selectUnit(node.id);

    // quando selezioni una unità, ricarica sempre:
    this.store.refreshAllowedRoles(node.id);
    this.store.refreshAssignments(node.id);
  }

  // DIPENDENTI
  openCreateEmployeeModal(): void {
    this.empError = '';
    this.empForm = { name: '', surname: '' };
    this.isCreateEmployeeOpen = true;
  }

  closeCreateEmployeeModal(): void {
    this.isCreateEmployeeOpen = false;
    this.empError = '';
  }

  submitCreateEmployee(): void {
    const name = this.empForm.name.trim();
    const surname = this.empForm.surname.trim();

    if (!name) { this.empError = 'Nome obbligatorio'; return; }
    if (!surname) { this.empError = 'Cognome obbligatorio'; return; }

    this.store.createEmployee({ name, surname });
    this.closeCreateEmployeeModal();

    this.store.refreshEmployees();
  }

  onDeleteEmployee(id: string): void {
    if (!confirm('Vuoi eliminare questo dipendente?')) return;

    this.store.deleteEmployee(id);
    this.store.refreshEmployees();

    const selectedUnitId = this.store.selectedUnitIdValue;
    if (selectedUnitId) {
      this.store.refreshAssignments(selectedUnitId);
    }
  }

  // UNITÀ
  openCreateUnitModal(selectedUnitId: string | null): void {
    this.formError = '';
    this.isCreateUnitOpen = true;
    this.unitForm = { name: '', parentId: selectedUnitId };
  }

  closeCreateUnitModal(): void {
    this.isCreateUnitOpen = false;
    this.formError = '';
  }

  submitCreateUnit(): void {
    const name = this.unitForm.name.trim();
    if (!name) {
      this.formError = 'Nome obbligatorio';
      return;
    }

    this.store.createUnit({ name, parentId: this.unitForm.parentId });
    this.closeCreateUnitModal();
    this.store.refreshOrgChart();
  }

  onDeleteUnit(unitId: string): void {
    this.unitDeleteError = '';
    if (!confirm('Vuoi eliminare questa unità?')) return;

    this.store.deleteUnit(unitId).subscribe({
      next: () => {
        this.store.refreshOrgChart();
        this.unitDeleteError = '';
      },
      error: (err: any) => {
        const backendMsg =
          (typeof err?.error === 'string' ? err.error : err?.error?.message) ??
          err?.message ??
          'Impossibile eliminare unità';

        this.unitDeleteError = backendMsg;
        this.store.refreshOrgChart();
      }
    });
  }

  // RUOLI AMMESSI
  openAllowedRolesModal(unitId: string): void {
    this.rolesModalError = '';
    this.rolesSearch = '';
    this.newRoleName = '';
    this.isAllowedRolesOpen = true;
    this.rolesUnitId = unitId;

    this.store.refreshRoles();
    this.store.refreshAllowedRoles(unitId);

    this.allowedRoleIds = new Set();
    this.allowedRoles$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(list => {
        this.allowedRoleIds = new Set(list.map(r => r.id));
      });
  }

  closeAllowedRolesModal(): void {
    this.isAllowedRolesOpen = false;
    this.rolesModalError = '';
    this.rolesUnitId = null;
    this.allowedRoleIds = new Set();
  }

  isRoleAllowed(roleId: string): boolean {
    return this.allowedRoleIds.has(roleId);
  }

  toggleRole(role: RoleDto, checked: boolean): void {
    const unitId = this.rolesUnitId;
    if (!unitId) return;

    this.rolesModalError = '';

    const req$ = checked
      ? this.store.allowRole(unitId, role.id)
      : this.store.disallowRole(unitId, role.id);

    req$.subscribe({
      next: () => {},
      error: (err: any) => {
        const msg =
          (typeof err?.error === 'string' ? err.error : err?.error?.message) ??
          err?.message ??
          'Operazione non riuscita';
        this.rolesModalError = msg;

        this.store.refreshAllowedRoles(unitId);
      }
    });
  }

  submitCreateRole(): void {
    const name = this.newRoleName.trim();
    if (!name) {
      this.rolesModalError = 'Nome ruolo obbligatorio';
      return;
    }

    this.rolesModalError = '';

    this.store.createRole(name).subscribe({
      next: () => { this.newRoleName = ''; },
      error: (err: any) => {
        const msg =
          (typeof err?.error === 'string' ? err.error : err?.error?.message) ??
          err?.message ??
          'Impossibile creare ruolo';
        this.rolesModalError = msg;
      }
    });
  }

  // ASSEGNAZIONI
  openAssignModal(unitId: string): void {
    this.assignError = '';
    this.isAssignOpen = true;

    this.store.refreshEmployees();
    this.store.refreshAllowedRoles(unitId);

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');

    this.assignForm = {
      employeeId: '',
      roleId: '',
      startDate: `${yyyy}-${mm}-${dd}`
    };

    //  reset filtro ruoli
    this.selectedAssignEmployeeId$.next('');
  }

  closeAssignModal(): void {
    this.isAssignOpen = false;
    this.assignError = '';
  }

  submitAssignment(unitId: string): void {
    this.assignError = '';

    if (!this.assignForm.employeeId) {
      this.assignError = 'Seleziona un dipendente';
      return;
    }
    if (!this.assignForm.roleId) {
      this.assignError = 'Seleziona un ruolo (ammesso per questa unità)';
      return;
    }

    this.store.createAssignment({
      unitId,
      employeeId: this.assignForm.employeeId,
      roleId: this.assignForm.roleId,
      startDate: this.assignForm.startDate || null
    }).subscribe({
      next: () => {
        this.closeAssignModal();
      },
      error: (err: any) => {
        const msg =
          (typeof err?.error === 'string' ? err.error : err?.error?.message) ??
          err?.message ??
          'Impossibile assegnare';
        this.assignError = msg;
      }
    });
  }

  removeAssignment(unitId: string, employeeId: string, roleId: string): void {
    if (!confirm('Vuoi rimuovere questa assegnazione?')) return;

    this.store.deleteAssignment(unitId, employeeId, roleId).subscribe({
      next: () => {
        //  aggiorna subito la lista a destra (e i ruoli disponibili nel modal)
        this.store.refreshAssignments(unitId);
      },
      error: (err: any) => {
        const msg =
          (typeof err?.error === 'string' ? err.error : err?.error?.message) ??
          err?.message ??
          'Impossibile rimuovere assegnazione';
        this.unitDeleteError = msg;
      }
    });
  }

}
