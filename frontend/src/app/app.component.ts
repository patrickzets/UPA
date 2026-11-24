import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (!noTelao) {
      <header class="topo">
        <a class="marca" routerLink="/">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 3h6v6h6v6h-6v6H9v-6H3V9h6z" fill="currentColor"/></svg>
          <span>UPA 24h<small>Painel de atendimento</small></span>
        </a>
        <nav>
          <a routerLink="/" routerLinkActive="ativo" [routerLinkActiveOptions]="{ exact: true }">Painel</a>
          <a routerLink="/pacientes" routerLinkActive="ativo">Pacientes</a>
          <a routerLink="/agenda" routerLinkActive="ativo">Agenda</a>
          <a routerLink="/historico" routerLinkActive="ativo">Histórico</a>
          <a routerLink="/configuracoes" routerLinkActive="ativo">Configurações</a>
        </nav>
        <span class="relogio">{{ hora }}</span>
        <a class="btn" routerLink="/telao">Abrir telão</a>
      </header>
    }
    <router-outlet></router-outlet>
  `
})
export class AppComponent implements OnInit, OnDestroy {
  noTelao = false;
  hora = '';
  private relogio?: ReturnType<typeof setInterval>;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(e => (this.noTelao = (e as NavigationEnd).urlAfterRedirects.startsWith('/telao')));
  }

  ngOnInit() {
    this.atualizarHora();
    this.relogio = setInterval(() => this.atualizarHora(), 1000);
  }

  ngOnDestroy() {
    clearInterval(this.relogio);
  }

  private atualizarHora() {
    this.hora = new Date().toLocaleTimeString('pt-BR');
  }
}
