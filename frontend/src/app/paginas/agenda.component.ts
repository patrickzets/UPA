import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Agendamento } from '../modelos';
import { UpaService } from '../upa.service';

@Component({
  selector: 'app-agenda',
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (erro) {
      <div class="erro">{{ erro }}</div>
    }

    <div class="caixa">
      <h3>Nova consulta</h3>
      <input placeholder="CPF" [(ngModel)]="novo.cpf">
      <input type="date" [(ngModel)]="novo.data">
      <input type="time" [(ngModel)]="novo.horario">
      <input placeholder="Especialidade" [(ngModel)]="novo.especialidade">
      <button (click)="agendar()">Agendar</button>
    </div>

    <div class="caixa">
      <h3>Agenda ordenada</h3>
      <table>
        <tr><th>Data</th><th>Horario</th><th>Especialidade</th><th>CPF</th><th></th></tr>
        @for (a of agenda; track a.id) {
          <tr>
            <td>{{ a.data }}</td>
            <td>{{ a.horario }}</td>
            <td>{{ a.especialidade }}</td>
            <td>{{ a.cpf }}</td>
            <td><button (click)="remover(a.id)">Remover</button></td>
          </tr>
        }
      </table>
    </div>
  `
})
export class AgendaComponent implements OnInit {
  agenda: Agendamento[] = [];
  novo = { cpf: '', data: '', horario: '', especialidade: '' };
  erro = '';

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.upa.agenda().subscribe(a => (this.agenda = a));
  }

  agendar() {
    this.erro = '';
    this.upa.agendar(this.novo).subscribe({
      next: () => this.carregar(),
      error: e => (this.erro = e.error?.erro ?? 'Erro ao agendar')
    });
  }

  remover(id: number) {
    this.upa.removerAgendamento(id).subscribe(() => this.carregar());
  }
}
