import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Configuracao } from '../modelos';
import { UpaService } from '../upa.service';

@Component({
  selector: 'app-configuracoes',
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (mensagem) {
      <div class="erro">{{ mensagem }}</div>
    }

    <div class="caixa">
      <h3>Fechamento aleatorio de consultorios</h3>
      <label>
        <input type="checkbox" [(ngModel)]="config.fechamentoAutomatico"> Ativar fechamento automatico
      </label>
      <br>
      <label>Sortear um consultorio a cada
        <input type="number" [(ngModel)]="config.intervaloFechamentoSeg"> segundos
      </label>
      <br>
      <label>Ele fica fechado por
        <input type="number" [(ngModel)]="config.duracaoFechamentoSeg"> segundos
      </label>
      <br>
      <button (click)="salvar()">Salvar</button>
    </div>
  `
})
export class ConfiguracoesComponent implements OnInit {
  config: Configuracao = { intervaloFechamentoSeg: 60, duracaoFechamentoSeg: 30, fechamentoAutomatico: true };
  mensagem = '';

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.upa.configuracao().subscribe(c => (this.config = c));
  }

  salvar() {
    this.upa.salvarConfiguracao(this.config).subscribe({
      next: () => (this.mensagem = 'Configuracao salva'),
      error: e => (this.mensagem = e.error?.erro ?? 'Erro ao salvar')
    });
  }
}
