import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EmployeeDto {
  id: string;
  name: string;
  surname: string;
}

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  constructor(private http: HttpClient) {}

  list(): Observable<EmployeeDto[]> {
    return this.http.get<EmployeeDto[]>('/api/employees');
  }

  create(body: { name: string; surname: string }): Observable<EmployeeDto> {
    return this.http.post<EmployeeDto>('/api/employees', body);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/employees/${id}`);
  }
}
