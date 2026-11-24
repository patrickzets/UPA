import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Chamada, Consultorio } from '../modelos';
import { UpaService } from '../upa.service';
import { SeloComponent, abreviar, horaMinuto } from '../comum';

// tempo mínimo que cada chamada fica na tela antes da próxima
const TEMPO_CHAMADA = 8000;

const MENSAGENS = [
  'O atendimento segue a gravidade de cada caso, e não a ordem de chegada.',
  'Mudou alguma coisa no seu estado? Avise a equipe da recepção.',
  'Quando for a sua vez, seu nome e sua senha aparecem neste painel.',
  'Mantenha seu documento em mãos para agilizar o atendimento.'
];

@Component({
  selector: 'app-telao',
  standalone: true,
  imports: [FormsModule, RouterLink, SeloComponent],
  template: `
    <div class="telao">
      <div class="telao-ctl">
        <a class="btn" routerLink="/">← Voltar ao painel</a>
        <button class="btn" (click)="telaCheia()">Tela cheia</button>
        <label><input type="checkbox" [(ngModel)]="som" (change)="testarSom()"> Aviso sonoro de nova chamada</label>
      </div>

      <div class="telao-area">
        <div class="tv" #tv>
          <div class="tv-cab">
            <div class="marca">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 3h6v6h6v6h-6v6H9v-6H3V9h6z" fill="currentColor"/></svg>
              <div>UPA 24h<small>Unidade de Pronto Atendimento</small></div>
            </div>
            <div class="hora">{{ hora }}<small>{{ data }}</small></div>
          </div>

          <div class="tv-meio">
            <div class="tv-chamada" [class.alerta]="alerta" aria-live="assertive">
              <div class="cab-ch">
                <span class="rot">Chamada agora</span>
                @if (atual) { <app-selo [cor]="atual.cor" /> }
              </div>
              <div class="nome">
                <strong>{{ atual ? abreviar(atual.nome) : 'Aguardando chamadas' }}</strong>
                @if (atual) {
                  <span class="senha"><small>Senha</small>{{ atual.senha }}</span>
                }
              </div>
              <div class="destino">
                <div class="txt"><span>Dirija-se ao</span><b>Consultório</b></div>
                <div class="numero">{{ atual?.consultorio ?? '–' }}</div>
              </div>
            </div>

            <div class="tv-ult">
              <span class="rot">Chamadas anteriores</span>
              <ol>
                @for (c of anteriores; track c.id) {
                  <li>
                    <span class="quem">{{ abreviar(c.nome) }}<small><em>{{ c.senha }}</em> · {{ horaMinuto(c.hora) }}</small></span>
                    <b class="cons"><small>Cons.</small>{{ c.consultorio }}</b>
                  </li>
                }
              </ol>
            </div>
          </div>

          <div class="tv-salas">
            <span class="rot tit">Consultórios</span>
            @for (s of consultorios; track s.numero) {
              <div class="s" [class.ocupado]="s.aberto && s.ocupado" [class.fechado]="!s.aberto">
                <span class="lbl">Consultório</span>
                <b>{{ s.numero }}</b>
                <span class="st">{{ !s.aberto ? 'Fechado' : s.ocupado ? 'Atendendo' : 'Livre' }}</span>
              </div>
            }
          </div>

          <div class="tv-aviso">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z" fill="currentColor"/></svg>
            <span>{{ mensagens[indiceMensagem] }}</span>
            <span class="pontos">
              @for (m of mensagens; track $index) { <i [class.on]="$index === indiceMensagem"></i> }
            </span>
          </div>
        </div>
      </div>
    </div>
  `
})
export class TelaoComponent implements OnInit, OnDestroy {
  @ViewChild('tv') tv!: ElementRef<HTMLElement>;

  consultorios: Consultorio[] = [];
  atual: Chamada | null = null;
  anteriores: Chamada[] = [];
  alerta = false;
  som = false;
  hora = '';
  data = '';
  mensagens = MENSAGENS;
  indiceMensagem = 0;

  abreviar = abreviar;
  horaMinuto = horaMinuto;

  private jaVistas = new Set<number>();
  private pendentes: Chamada[] = [];
  private primeiraLeitura = true;
  private livreEm = 0;
  private proxima?: ReturnType<typeof setTimeout>;
  private fimAlerta?: ReturnType<typeof setTimeout>;
  private atualizacao?: ReturnType<typeof setInterval>;
  private rodizio?: ReturnType<typeof setInterval>;
  private audio?: AudioContext;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.atualizar();
    this.atualizacao = setInterval(() => this.atualizar(), 1000);
    this.rodizio = setInterval(() => (this.indiceMensagem = (this.indiceMensagem + 1) % MENSAGENS.length), 9000);
  }

  ngOnDestroy() {
    clearInterval(this.atualizacao);
    clearInterval(this.rodizio);
    clearTimeout(this.proxima);
    clearTimeout(this.fimAlerta);
    this.audio?.close();
  }

  atualizar() {
    const agora = new Date();
    this.hora = agora.toLocaleTimeString('pt-BR');
    this.data = agora.toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long' });

    this.upa.consultorios().subscribe(c => (this.consultorios = c));
    this.upa.chamadas().subscribe(lista => this.receberChamadas(lista));
  }

  private receberChamadas(lista: Chamada[]) {
    if (this.primeiraLeitura) {
      lista.forEach(c => this.jaVistas.add(c.id));
      this.atual = lista[0] ?? null;
      this.anteriores = lista.slice(1, 5);
      this.primeiraLeitura = false;
      return;
    }
    const novas = lista.filter(c => !this.jaVistas.has(c.id)).reverse();
    novas.forEach(c => this.jaVistas.add(c.id));
    this.pendentes.push(...novas);
    this.mostrarProxima();
  }

  // se dois consultórios liberam juntos, as chamadas entram em fila para uma não atropelar a outra
  private mostrarProxima() {
    if (this.proxima || !this.pendentes.length) {
      return;
    }
    const espera = Math.max(0, this.livreEm - Date.now());
    this.proxima = setTimeout(() => {
      this.proxima = undefined;
      const chamada = this.pendentes.shift()!;
      if (this.atual) {
        this.anteriores = [this.atual, ...this.anteriores].slice(0, 4);
      }
      this.atual = chamada;
      this.alerta = true;
      clearTimeout(this.fimAlerta);
      this.fimAlerta = setTimeout(() => (this.alerta = false), TEMPO_CHAMADA);
      if (this.som) {
        this.tocar();
      }
      this.livreEm = Date.now() + TEMPO_CHAMADA;
      this.mostrarProxima();
    }, espera);
  }

  telaCheia() {
    this.tv.nativeElement.requestFullscreen?.().catch(() => {});
  }

  testarSom() {
    if (this.som) {
      this.tocar();
    }
  }

  private tocar() {
    try {
      this.audio = this.audio ?? new AudioContext();
      const inicio = this.audio.currentTime;
      [[880, 0], [660, 0.28]].forEach(([frequencia, atraso]) => {
        const osc = this.audio!.createOscillator();
        const volume = this.audio!.createGain();
        osc.type = 'sine';
        osc.frequency.value = frequencia;
        volume.gain.setValueAtTime(0, inicio + atraso);
        volume.gain.linearRampToValueAtTime(0.25, inicio + atraso + 0.02);
        volume.gain.exponentialRampToValueAtTime(0.001, inicio + atraso + 0.6);
        osc.connect(volume).connect(this.audio!.destination);
        osc.start(inicio + atraso);
        osc.stop(inicio + atraso + 0.65);
      });
    } catch {
      this.som = false;
    }
  }
}
