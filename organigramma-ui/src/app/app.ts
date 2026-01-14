import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {OrgchartComponent} from './orgchart/orgchart.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, OrgchartComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('organigramma-ui');
}
