import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Paciente, Sintoma } from '../modelos';
import { UpaService } from '../upa.service';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (erro) {
      <div class="erro">{{ erro }}</div>
    }

    <div class="caixa">
      <h3>Novo paciente</h3>
      <input placeholder="CPF" [(ngModel)]="novo.cpf">
      <input placeholder="Nome" [(ngModel)]="novo.nome">
      <input placeholder="Idade" type="number" [(ngModel)]="novo.idade">
      <input placeholder="Solicitacao" [(ngModel)]="novo.descricao">
      <button (click)="cadastrar()">Cadastrar</button>
      <button (click)="desfazer()">Desfazer ultima operacao</button>
    </div>

    <div class="caixa">
      <h3>Registrar chegada</h3>
      <select [(ngModel)]="chegada.cpf">
        <option value="">Paciente</option>
        @for (p of pacientes; track p.cpf) {
          <option [value]="p.cpf">{{ p.nome }}</option>
        }
      </select>
      <select [(ngModel)]="chegada.cor">
        <option value="VERMELHO">Vermelho</option>
        <option value="AMARELO">Amarelo</option>
        <option value="VERDE">Verde</option>
      </select>
      <div>
        @for (s of sintomas; track s.id) {
          <label style="display: inline-block; width: 260px">
            <input type="checkbox" (change)="marcar(s.id)"> {{ s.nome }} ({{ s.pontos }} pts)
          </label>
        }
      </div>
      <button (click)="registrarChegada()">Enviar para a recepcao</button>
    </div>

    <div class="caixa">
      <h3>Pacientes cadastrados</h3>
      <table>
        <tr><th>CPF</th><th>Nome</th><th>Idade</th><th>Solicitacao</th><th>Status</th><th></th></tr>
        @for (p of pacientes; track p.cpf) {
          <tr>
            <td>{{ p.cpf }}</td>
            <td>{{ p.nome }}</td>
            <td>{{ p.idade }}</td>
            <td>{{ p.descricao }}</td>
            <td>{{ p.status }}</td>
            <td><button (click)="remover(p.cpf)">Remover</button></td>
          </tr>
        }
      </table>
    </div>
  `
})
export class PacientesComponent implements OnInit {
  pacientes: Paciente[] = [];
  sintomas: Sintoma[] = [];
  novo = { cpf: '', nome: '', idade: 0, descricao: '' };
  chegada = { cpf: '', cor: 'VERDE', sintomas: [] as number[] };
  erro = '';

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.carregar();
    this.upa.sintomas().subscribe(s => (this.sintomas = s));
  }

  carregar() {
    this.upa.pacientes().subscribe(p => (this.pacientes = p));
  }

  cadastrar() {
    this.erro = '';
    this.upa.cadastrarPaciente(this.novo).subscribe({
      next: () => {
        this.novo = { cpf: '', nome: '', idade: 0, descricao: '' };
        this.carregar();
      },
      error: e => (this.erro = e.error?.erro ?? 'Erro ao cadastrar')
    });
  }

  remover(cpf: string) {
    this.erro = '';
    this.upa.removerPaciente(cpf).subscribe({
      next: () => this.carregar(),
      error: e => (this.erro = e.error?.erro ?? 'Erro ao remover')
    });
  }

  marcar(id: number) {
    const i = this.chegada.sintomas.indexOf(id);
    if (i >= 0) {
      this.chegada.sintomas.splice(i, 1);
    } else {
      this.chegada.sintomas.push(id);
    }
  }

  registrarChegada() {
    this.erro = '';
    this.upa.registrarChegada(this.chegada).subscribe({
      next: () => this.carregar(),
      error: e => (this.erro = e.error?.erro ?? 'Erro ao registrar chegada')
    });
  }

  desfazer() {
    this.erro = '';
    this.upa.desfazer().subscribe({
      next: r => {
        this.erro = r.mensagem;
        this.carregar();
      },
      error: e => (this.erro = e.error?.erro ?? 'Nada para desfazer')
    });
  }
}
