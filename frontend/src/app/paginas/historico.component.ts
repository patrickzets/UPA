import { Component, OnInit } from '@angular/core';
import { Atendimento } from '../modelos';
import { UpaService } from '../upa.service';

@Component({
  selector: 'app-historico',
  standalone: true,
  template: `
    @if (erro) {
      <div class="erro">{{ erro }}</div>
    }

    <div class="caixa">
      <h3>Navegar pelo historico</h3>
      <button (click)="ir('primeiro')">Primeiro</button>
      <button (click)="ir('anterior')">Anterior</button>
      <button (click)="ir('proximo')">Proximo</button>
      <button (click)="ir('ultimo')">Ultimo</button>
      @if (atual) {
        <p>
          <span [class]="atual.cor">{{ atual.cor }}</span>
          {{ atual.nomePaciente }} | {{ atual.pontuacao }} pts | consultorio {{ atual.consultorio }}
        </p>
      }
    </div>

    <div class="caixa">
      <h3>Lista completa</h3>
      <button (click)="carregar('inicio')">Do inicio ao fim</button>
      <button (click)="carregar('fim')">Do fim ao inicio</button>
      <table>
        <tr><th>#</th><th>Paciente</th><th>Cor</th><th>Pontos</th><th>Consultorio</th></tr>
        @for (a of lista; track a.id) {
          <tr>
            <td>{{ a.id }}</td>
            <td>{{ a.nomePaciente }}</td>
            <td [class]="a.cor">{{ a.cor }}</td>
            <td>{{ a.pontuacao }}</td>
            <td>{{ a.consultorio }}</td>
          </tr>
        }
      </table>
    </div>
  `
})
export class HistoricoComponent implements OnInit {
  lista: Atendimento[] = [];
  atual: Atendimento | null = null;
  erro = '';

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.carregar('inicio');
  }

  carregar(ordem: 'inicio' | 'fim') {
    this.upa.historico(ordem).subscribe(l => (this.lista = l));
  }

  ir(destino: 'primeiro' | 'ultimo' | 'proximo' | 'anterior') {
    this.erro = '';
    this.upa.navegarHistorico(destino).subscribe({
      next: a => (this.atual = a),
      error: e => (this.erro = e.error?.erro ?? 'Erro ao navegar')
    });
  }
}
