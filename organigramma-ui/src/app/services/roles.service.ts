import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RoleDto {
  id: string;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class RolesService {
  constructor(private http: HttpClient) {}

  listRoles(): Observable<RoleDto[]> {
    return this.http.get<RoleDto[]>('/api/roles');
  }

  createRole(body: { name: string }): Observable<RoleDto> {
    return this.http.post<RoleDto>('/api/roles', body);
  }

  listAllowedRoles(unitId: string): Observable<RoleDto[]> {
    return this.http.get<RoleDto[]>(`/api/units/${unitId}/allowed-roles`);
  }

  allowRole(unitId: string, roleId: string): Observable<void> {
    return this.http.post<void>(`/api/units/${unitId}/allowed-roles/${roleId}`, {});
  }

  disallowRole(unitId: string, roleId: string): Observable<void> {
    return this.http.delete<void>(`/api/units/${unitId}/allowed-roles/${roleId}`);
  }
}
