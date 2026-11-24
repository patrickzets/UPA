import { Component, OnInit } from '@angular/core';
import { Atendimento } from '../modelos';
import { UpaService } from '../upa.service';
import { SeloComponent, erroDaApi, horaMinuto } from '../comum';

type Destino = 'primeiro' | 'ultimo' | 'proximo' | 'anterior';

@Component({
  selector: 'app-historico',
  standalone: true,
  imports: [SeloComponent],
  template: `
    <div class="pagina">
      <div class="pagina-titulo">
        <div>
          <h1>Histórico</h1>
          <p>Atendimentos concluídos</p>
        </div>
      </div>

      @if (erro) {
        <div class="msg erro">{{ erro }}</div>
      }

      <div class="bloco">
        <header><b>Percorrer atendimentos</b><span class="estrutura">lista duplamente encadeada</span></header>
        <div class="navegador">
          <div class="acoes-form">
            <button class="btn" (click)="ir('primeiro')">« Primeiro</button>
            <button class="btn" (click)="ir('anterior')">‹ Anterior</button>
          </div>
          <div class="atual">
            @if (atual) {
              <strong>{{ atual.nomePaciente }}</strong>
              <div class="linha">
                <app-selo [cor]="atual.cor" />
                <span>{{ atual.pontuacao }} pts</span>
                <span>Consultório {{ atual.consultorio }}</span>
                <span class="num">{{ horaMinuto(atual.inicio) }} → {{ horaMinuto(atual.fim) }}</span>
              </div>
            } @else {
              <span class="vazio">Use os botões para andar pelo histórico</span>
            }
          </div>
          <div class="acoes-form">
            <button class="btn" (click)="ir('proximo')">Próximo ›</button>
            <button class="btn" (click)="ir('ultimo')">Último »</button>
          </div>
        </div>
      </div>

      <div class="bloco">
        <header>
          <b>Todos os atendimentos<span class="num">{{ lista.length }}</span></b>
          <div class="seg">
            <button [class.ativo]="ordem === 'inicio'" (click)="carregar('inicio')">Do primeiro ao último</button>
            <button [class.ativo]="ordem === 'fim'" (click)="carregar('fim')">Do último ao primeiro</button>
          </div>
        </header>
        <div class="tabela">
          <table>
            <thead><tr><th>#</th><th>Paciente</th><th>Risco</th><th>Pts</th><th>Consultório</th><th>Horário</th></tr></thead>
            <tbody>
              @for (a of lista; track a.id) {
                <tr [class.vem]="atual?.id === a.id">
                  <td class="pos">{{ a.id }}</td>
                  <td>{{ a.nomePaciente }}</td>
                  <td><app-selo [cor]="a.cor" /></td>
                  <td class="num">{{ a.pontuacao }}</td>
                  <td>{{ a.consultorio }}</td>
                  <td class="num">{{ horaMinuto(a.inicio) }} → {{ horaMinuto(a.fim) }}</td>
                </tr>
              } @empty {
                <tr><td colspan="6" class="vazio">Nenhum atendimento concluído ainda</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class HistoricoComponent implements OnInit {
  lista: Atendimento[] = [];
  atual: Atendimento | null = null;
  ordem: 'inicio' | 'fim' = 'inicio';
  erro = '';
  horaMinuto = horaMinuto;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.carregar('inicio');
  }

  carregar(ordem: 'inicio' | 'fim') {
    this.ordem = ordem;
    this.upa.historico(ordem).subscribe(l => (this.lista = l));
  }

  ir(destino: Destino) {
    this.erro = '';
    this.upa.navegarHistorico(destino).subscribe({
      next: a => (this.atual = a),
      error: e => (this.erro = erroDaApi(e, 'Erro ao navegar'))
    });
  }
}
