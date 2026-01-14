import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AssignmentDto {
  unitId: string;
  employeeId: string;
  employeeName: string;
  employeeSurname: string;
  roleId: string;
  roleName: string;
  startDate: string | null; // LocalDate -> string "YYYY-MM-DD"
}

export interface CreateAssignmentRequest {
  employeeId: string;
  unitId: string;
  roleId: string;
  startDate?: string | null;
}

@Injectable({ providedIn: 'root' })
export class AssignmentsService {
  constructor(private http: HttpClient) {}

  listByUnit(unitId: string): Observable<AssignmentDto[]> {
    return this.http.get<AssignmentDto[]>(`/api/units/${unitId}/assignments`);
  }

  create(body: CreateAssignmentRequest): Observable<void> {
    return this.http.post<void>('/api/assignments', body);
  }

  delete(unitId: string, employeeId: string, roleId: string): Observable<void> {
    return this.http.delete<void>(`/api/units/${unitId}/assignments/${employeeId}/${roleId}`);
  }
}
