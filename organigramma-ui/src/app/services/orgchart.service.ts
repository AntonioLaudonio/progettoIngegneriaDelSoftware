import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UnitNodeDto {
  id: string;
  name: string;
  children: UnitNodeDto[];
}

@Injectable({ providedIn: 'root' })
export class OrgchartService {
  constructor(private http: HttpClient) {}

  getOrgchart(): Observable<UnitNodeDto | null> {
    return this.http.get<UnitNodeDto | null>('/api/orgchart');
  }


  createUnit(body: { name: string; parentId?: string | null }) {
    return this.http.post('/api/units', body);
  }

  deleteUnit(unitId: string) {
    return this.http.delete<void>(`/api/units/${unitId}`);
  }




}
