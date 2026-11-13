import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav>
      <a routerLink="/" routerLinkActive="ativo" [routerLinkActiveOptions]="{ exact: true }">Painel</a>
      <a routerLink="/pacientes" routerLinkActive="ativo">Pacientes</a>
      <a routerLink="/agenda" routerLinkActive="ativo">Agenda</a>
      <a routerLink="/historico" routerLinkActive="ativo">Historico</a>
      <a routerLink="/configuracoes" routerLinkActive="ativo">Configuracoes</a>
    </nav>
    <main>
      <router-outlet></router-outlet>
    </main>
  `
})
export class AppComponent {}
