import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cor, Paciente, Sintoma, StatusPaciente } from '../modelos';
import { UpaService } from '../upa.service';
import { SeloComponent, abreviar, erroDaApi } from '../comum';

const NOME_STATUS: Record<StatusPaciente, string> = {
  CADASTRADO: 'Cadastrado',
  NA_RECEPCAO: 'Na recepção',
  AGUARDANDO: 'Aguardando',
  EM_ATENDIMENTO: 'Em atendimento',
  ATENDIDO: 'Atendido'
};

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [FormsModule, SeloComponent],
  template: `
    <div class="pagina">
      <div class="pagina-titulo">
        <div>
          <h1>Pacientes</h1>
          <p>Cadastro e chegada na recepção</p>
        </div>
        <button class="btn" (click)="desfazer()">Desfazer última operação</button>
      </div>

      @if (mensagem) {
        <div class="msg" [class.erro]="erro" [class.ok]="!erro">{{ mensagem }}</div>
      }

      <div class="duas-colunas">
        <form class="bloco" (ngSubmit)="cadastrar()">
          <header><b>Novo paciente</b></header>
          <div class="form">
            <label class="campo"><span>CPF</span><input name="cpf" [(ngModel)]="novo.cpf" inputmode="numeric" maxlength="11"></label>
            <label class="campo"><span>Idade</span><input name="idade" type="number" min="0" max="130" [(ngModel)]="novo.idade"></label>
          </div>
          <label class="campo"><span>Nome completo</span><input name="nome" [(ngModel)]="novo.nome"></label>
          <label class="campo"><span>Motivo da procura</span><input name="descricao" [(ngModel)]="novo.descricao"></label>
          <div class="acoes-form"><button class="btn pri" type="submit">Cadastrar</button></div>
        </form>

        <form class="bloco" (ngSubmit)="registrarChegada()">
          <header><b>Registrar chegada</b><span class="estrutura">entra na fila da recepção</span></header>
          <label class="campo">
            <span>Paciente</span>
            <select name="paciente" [(ngModel)]="chegada.cpf">
              <option value="">Escolha um paciente</option>
              @for (p of podemChegar(); track p.cpf) {
                <option [value]="p.cpf">{{ p.nome }}</option>
              }
            </select>
          </label>
          <div class="campo">
            <span>Classificação de risco</span>
            <div class="cores">
              @for (c of cores; track c) {
                <label>
                  <input type="radio" name="cor" [value]="c" [(ngModel)]="chegada.cor">
                  <span class="opcao"><app-selo [cor]="c" /></span>
                </label>
              }
            </div>
          </div>
          <div class="campo">
            <span>Sintomas ({{ chegada.sintomas.length }} marcados · {{ pontosMarcados() }} pts)</span>
            <div class="sintomas">
              @for (s of sintomas; track s.id) {
                <label [class.marcado]="marcado(s.id)">
                  <input type="checkbox" [checked]="marcado(s.id)" (change)="marcar(s.id)">
                  {{ s.nome }}<span class="pts">{{ s.pontos }}</span>
                </label>
              }
            </div>
          </div>
          <div class="acoes-form"><button class="btn pri" type="submit">Enviar para a recepção</button></div>
        </form>
      </div>

      <div class="bloco">
        <header><b>Pacientes cadastrados<span class="num">{{ pacientes.length }}</span></b><span class="estrutura">lista simples</span></header>
        <div class="tabela">
          <table>
            <thead><tr><th>Nome</th><th>CPF</th><th>Idade</th><th>Motivo</th><th>Risco</th><th>Senha</th><th>Situação</th><th></th></tr></thead>
            <tbody>
              @for (p of pacientes; track p.cpf) {
                <tr>
                  <td><b [title]="p.nome">{{ abreviar(p.nome, 32) }}</b></td>
                  <td class="num">{{ p.cpf }}</td>
                  <td>{{ p.idade }}</td>
                  <td>{{ p.descricao }}</td>
                  <td>@if (p.cor) { <app-selo [cor]="p.cor" /> }</td>
                  <td>@if (p.senha) { <span class="tk">{{ p.senha }}</span> }</td>
                  <td><span class="status" [class]="'status ' + p.status">{{ nomeStatus[p.status] }}</span></td>
                  <td><button class="btn mini" (click)="remover(p.cpf)">Remover</button></td>
                </tr>
              } @empty {
                <tr><td colspan="8" class="vazio">Nenhum paciente cadastrado</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class PacientesComponent implements OnInit {
  pacientes: Paciente[] = [];
  sintomas: Sintoma[] = [];
  cores: Cor[] = ['VERMELHO', 'AMARELO', 'VERDE'];
  novo = { cpf: '', nome: '', idade: 0, descricao: '' };
  chegada = { cpf: '', cor: 'VERDE' as Cor, sintomas: [] as number[] };
  mensagem = '';
  erro = false;
  nomeStatus = NOME_STATUS;
  abreviar = abreviar;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.carregar();
    this.upa.sintomas().subscribe(s => (this.sintomas = s));
  }

  carregar() {
    this.upa.pacientes().subscribe(p => (this.pacientes = p));
  }

  podemChegar(): Paciente[] {
    return this.pacientes.filter(p => p.status === 'CADASTRADO' || p.status === 'ATENDIDO');
  }

  marcado(id: number): boolean {
    return this.chegada.sintomas.includes(id);
  }

  marcar(id: number) {
    this.chegada.sintomas = this.marcado(id)
      ? this.chegada.sintomas.filter(s => s !== id)
      : [...this.chegada.sintomas, id];
  }

  pontosMarcados(): number {
    return this.sintomas.filter(s => this.marcado(s.id)).reduce((soma, s) => soma + s.pontos, 0);
  }

  cadastrar() {
    this.upa.cadastrarPaciente(this.novo).subscribe({
      next: p => {
        this.avisar(`${p.nome} cadastrado`, false);
        this.novo = { cpf: '', nome: '', idade: 0, descricao: '' };
        this.carregar();
      },
      error: e => this.avisar(erroDaApi(e, 'Erro ao cadastrar'), true)
    });
  }

  registrarChegada() {
    this.upa.registrarChegada(this.chegada).subscribe({
      next: p => {
        this.avisar(`${p.nome} está na recepção com a senha ${p.senha}`, false);
        this.chegada = { cpf: '', cor: 'VERDE', sintomas: [] };
        this.carregar();
      },
      error: e => this.avisar(erroDaApi(e, 'Erro ao registrar chegada'), true)
    });
  }

  remover(cpf: string) {
    this.upa.removerPaciente(cpf).subscribe({
      next: () => this.carregar(),
      error: e => this.avisar(erroDaApi(e, 'Erro ao remover'), true)
    });
  }

  desfazer() {
    this.upa.desfazer().subscribe({
      next: r => {
        this.avisar(r.mensagem, false);
        this.carregar();
      },
      error: e => this.avisar(erroDaApi(e, 'Nada para desfazer'), true)
    });
  }

  private avisar(texto: string, erro: boolean) {
    this.mensagem = texto;
    this.erro = erro;
  }
}
