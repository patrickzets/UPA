import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Agendamento, Paciente } from '../modelos';
import { UpaService } from '../upa.service';
import { erroDaApi } from '../comum';

@Component({
  selector: 'app-agenda',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="pagina">
      <div class="pagina-titulo">
        <div>
          <h1>Agenda</h1>
          <p>Consultas marcadas, sempre em ordem de data e horário</p>
        </div>
      </div>

      @if (mensagem) {
        <div class="msg" [class.erro]="erro" [class.ok]="!erro">{{ mensagem }}</div>
      }

      <form class="bloco" (ngSubmit)="agendar()">
        <header><b>Nova consulta</b></header>
        <div class="form">
          <label class="campo">
            <span>Paciente</span>
            <select name="cpf" [(ngModel)]="novo.cpf">
              <option value="">Escolha um paciente</option>
              @for (p of pacientes; track p.cpf) {
                <option [value]="p.cpf">{{ p.nome }}</option>
              }
            </select>
          </label>
          <label class="campo"><span>Data</span><input name="data" type="date" [(ngModel)]="novo.data"></label>
          <label class="campo"><span>Horário</span><input name="horario" type="time" [(ngModel)]="novo.horario"></label>
          <label class="campo"><span>Especialidade</span><input name="especialidade" [(ngModel)]="novo.especialidade"></label>
          <button class="btn pri" type="submit">Agendar</button>
        </div>
      </form>

      <div class="bloco">
        <header><b>Consultas<span class="num">{{ agenda.length }}</span></b><span class="estrutura">lista ordenada</span></header>
        <div class="tabela">
          <table>
            <thead><tr><th>Data</th><th>Horário</th><th>Paciente</th><th>Especialidade</th><th></th></tr></thead>
            <tbody>
              @for (a of agenda; track a.id) {
                <tr>
                  <td class="num">{{ formatarData(a.data) }}</td>
                  <td class="num">{{ a.horario.substring(0, 5) }}</td>
                  <td>{{ nomeDoPaciente(a.cpf) }}<small class="num">{{ a.cpf }}</small></td>
                  <td>{{ a.especialidade }}</td>
                  <td><button class="btn mini" (click)="remover(a.id)">Remover</button></td>
                </tr>
              } @empty {
                <tr><td colspan="5" class="vazio">Nenhuma consulta marcada</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class AgendaComponent implements OnInit {
  agenda: Agendamento[] = [];
  pacientes: Paciente[] = [];
  novo = { cpf: '', data: '', horario: '', especialidade: '' };
  mensagem = '';
  erro = false;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.carregar();
    this.upa.pacientes().subscribe(p => (this.pacientes = p));
  }

  carregar() {
    this.upa.agenda().subscribe(a => (this.agenda = a));
  }

  nomeDoPaciente(cpf: string): string {
    return this.pacientes.find(p => p.cpf === cpf)?.nome ?? 'Paciente removido';
  }

  formatarData(data: string): string {
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  agendar() {
    this.upa.agendar(this.novo).subscribe({
      next: () => {
        this.mensagem = 'Consulta agendada';
        this.erro = false;
        this.novo = { cpf: '', data: '', horario: '', especialidade: '' };
        this.carregar();
      },
      error: e => {
        this.mensagem = erroDaApi(e, 'Erro ao agendar');
        this.erro = true;
      }
    });
  }

  remover(id: number) {
    this.upa.removerAgendamento(id).subscribe(() => this.carregar());
  }
}
