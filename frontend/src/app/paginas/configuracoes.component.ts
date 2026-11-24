import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Configuracao } from '../modelos';
import { UpaService } from '../upa.service';
import { erroDaApi } from '../comum';

@Component({
  selector: 'app-configuracoes',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="pagina">
      <div class="pagina-titulo">
        <div>
          <h1>Configurações</h1>
          <p>Fechamento aleatório dos consultórios</p>
        </div>
      </div>

      @if (mensagem) {
        <div class="msg" [class.erro]="erro" [class.ok]="!erro">{{ mensagem }}</div>
      }

      <form class="bloco" style="max-width: 640px" (ngSubmit)="salvar()">
        <label class="interruptor">
          <input type="checkbox" name="automatico" [(ngModel)]="config.fechamentoAutomatico">
          <span>Fechar consultórios aleatoriamente</span>
        </label>
        <div class="form">
          <label class="campo">
            <span>Sortear um consultório a cada (segundos)</span>
            <input type="number" name="intervalo" min="1" [(ngModel)]="config.intervaloFechamentoSeg" [disabled]="!config.fechamentoAutomatico">
          </label>
          <label class="campo">
            <span>Tempo que ele fica fechado (segundos)</span>
            <input type="number" name="duracao" min="1" [(ngModel)]="config.duracaoFechamentoSeg" [disabled]="!config.fechamentoAutomatico">
          </label>
        </div>
        <p class="legenda">Pelo menos um consultório sempre fica aberto.</p>
        <div class="acoes-form"><button class="btn pri" type="submit">Salvar</button></div>
      </form>
    </div>
  `
})
export class ConfiguracoesComponent implements OnInit {
  config: Configuracao = { intervaloFechamentoSeg: 60, duracaoFechamentoSeg: 30, fechamentoAutomatico: true };
  mensagem = '';
  erro = false;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.upa.configuracao().subscribe(c => (this.config = c));
  }

  salvar() {
    this.upa.salvarConfiguracao(this.config).subscribe({
      next: () => {
        this.mensagem = 'Configuração salva';
        this.erro = false;
      },
      error: e => {
        this.mensagem = erroDaApi(e, 'Erro ao salvar');
        this.erro = true;
      }
    });
  }
}
