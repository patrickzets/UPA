import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { Chamada, Consultorio, Cor, EstadoFila, FilaCircular, Paciente } from '../modelos';
import { UpaService } from '../upa.service';
import { META_ESPERA, SeloComponent, abreviar, erroDaApi, formatarMinutos, horaMinuto } from '../comum';

const RAIO = 24;
const CIRCUNFERENCIA = 2 * Math.PI * RAIO;

@Component({
  selector: 'app-painel',
  standalone: true,
  imports: [SeloComponent],
  template: `
    <div class="pagina">
      @if (mensagem) {
        <div class="msg" [class.erro]="erro" [class.ok]="!erro">{{ mensagem }}</div>
      }

      <div class="kpis">
        <div class="kpi r">
          <svg viewBox="0 0 12 12" aria-hidden="true"><path d="M6 .8 11.2 6 6 11.2.8 6Z" fill="currentColor"/></svg>
          <b>{{ contarCor('VERMELHO') }}</b><span>emergência</span>
        </div>
        <div class="kpi y">
          <svg viewBox="0 0 12 12" aria-hidden="true"><path d="M6 1 11.4 10.6H.6Z" fill="currentColor"/></svg>
          <b>{{ contarCor('AMARELO') }}</b><span>urgente</span>
        </div>
        <div class="kpi g">
          <svg viewBox="0 0 12 12" aria-hidden="true"><circle cx="6" cy="6" r="5" fill="currentColor"/></svg>
          <b>{{ contarCor('VERDE') }}</b><span>pouco urgente</span>
        </div>
        <div class="kpi"><b>{{ esperaMedia() }}</b><span>min de espera média</span></div>
        <div class="kpi"><b>{{ atendidos }}</b><span>atendidos hoje</span></div>
      </div>

      <section class="chamada">
        @for (c of chamadaAtual(); track c.id) {
          <div class="quem entra">
            <span class="rot">Chamada agora</span>
            <strong [title]="c.nome">{{ abreviar(c.nome, 28) }}</strong>
            <div class="det"><app-selo [cor]="c.cor" /><span class="tk">Senha {{ c.senha }}</span></div>
          </div>
          <div class="dest"><span>Consultório</span><b class="entra">{{ c.consultorio }}</b></div>
        } @empty {
          <div class="quem">
            <span class="rot">Chamada agora</span>
            <strong>—</strong>
            <div class="det">Ninguém foi chamado ainda</div>
          </div>
          <div class="dest"><span>Consultório</span><b>–</b></div>
        }
        <div class="ultimas">
          <span class="rot">Últimas chamadas</span>
          <ol>
            @for (c of chamadas.slice(1, 4); track c.id) {
              <li>
                <time>{{ horaMinuto(c.hora) }}</time>
                <span [title]="c.nome">{{ abreviar(c.nome, 18) }} <span class="tk">{{ c.senha }}</span></span>
                <span class="c">C{{ c.consultorio }}</span>
              </li>
            }
          </ol>
        </div>
      </section>

      <section class="salas">
        @for (s of consultorios; track s.numero) {
          <div class="sala" [class.fechado]="!s.aberto" [class.vez]="s.aberto && !s.ocupado && s.numero === vez()">
            <div class="cab-sala">
              <span class="nome-sala">Consultório<b>{{ s.numero }}</b></span>
              @if (!s.aberto) {
                <span class="estado fechado">Fechado</span>
              } @else if (s.ocupado) {
                <span class="estado ocupado">Atendendo</span>
              } @else {
                <span class="estado livre">Livre</span>
              }
            </div>

            <div class="meio">
              @if (!s.aberto) {
                <span class="info">
                  @if (s.reabreEmSegundos !== null) { Reabre em <b class="num">{{ s.reabreEmSegundos }}s</b> } @else { Fechado manualmente }
                </span>
              } @else if (s.ocupado) {
                <div class="pac">
                  <strong [title]="s.paciente">{{ abreviar(s.paciente!, 18) }}</strong>
                  <div class="linha">
                    @if (s.cor) { <app-selo [cor]="s.cor" /> }
                    @if (s.senha) { <span class="tk">{{ s.senha }}</span> }
                  </div>
                </div>
                @if (s.restamSegundos !== null) {
                  <div class="anel">
                    <svg viewBox="0 0 58 58">
                      <circle class="fundo" cx="29" cy="29" [attr.r]="raio" />
                      <circle class="valor" cx="29" cy="29" [attr.r]="raio"
                              [attr.stroke-dasharray]="circunferencia" [attr.stroke-dashoffset]="deslocamento(s)" />
                    </svg>
                    <b>{{ s.restamSegundos }}s<small>restam</small></b>
                  </div>
                }
              } @else if (s.numero === vez()) {
                <span class="vez-tag">Próximo a receber paciente</span>
              } @else {
                <span class="info">Aguardando paciente</span>
              }
            </div>

            <div class="acoes">
              @if (!s.aberto) {
                <button class="btn mini" (click)="abrir(s.numero)">Abrir</button>
              } @else {
                @if (s.ocupado) {
                  <button class="btn mini" (click)="concluir(s.numero)">Concluir</button>
                }
                <button class="btn mini" (click)="fechar(s.numero)">Fechar</button>
              }
            </div>
          </div>
        }
      </section>

      <section class="filas">
        <div class="bloco">
          <header><b>Recepção<span class="num">{{ fila?.recepcao?.length ?? 0 }}</span></b><span class="estrutura">fila simples</span></header>
          <ul class="recepcao">
            @for (p of (fila?.recepcao ?? []).slice(0, 4); track p.cpf; let i = $index) {
              <li>
                <div class="linha">
                  <b [title]="p.nome">{{ abreviar(p.nome, 22) }}</b>
                  @if (i === 0) { <span class="ptr">próximo a triar</span> }
                </div>
                <small>{{ p.idade }} anos · {{ p.descricao }}</small>
                <div class="linha esq">
                  @if (p.cor) { <app-selo [cor]="p.cor" /> }
                  @if (p.senha) { <span class="tk">{{ p.senha }}</span> }
                </div>
              </li>
            } @empty {
              <li class="mais">Ninguém aguardando triagem</li>
            }
            @if ((fila?.recepcao?.length ?? 0) > 4) {
              <li class="mais">e mais {{ fila!.recepcao.length - 4 }} aguardando triagem</li>
            }
          </ul>
        </div>

        <div class="bloco prioridade">
          <header>
            <b>Fila de atendimento<span class="num">{{ fila?.aguardando?.length ?? 0 }}</span></b>
            <span class="estrutura">fila de prioridade · risco › pontos › chegada</span>
          </header>
          <div class="tabela">
            <table>
              <thead><tr><th>#</th><th>Paciente</th><th>Risco</th><th>Pts</th><th>Espera / meta</th></tr></thead>
              <tbody>
                @for (p of (fila?.aguardando ?? []).slice(0, 6); track p.cpf; let i = $index) {
                  <tr [class.vem]="i === 0">
                    <td class="pos">{{ i + 1 }}</td>
                    <td>
                      <b [title]="p.nome">{{ abreviar(p.nome, 26) }}</b>
                      @if (p.senha) { <span class="tk">{{ p.senha }}</span> }
                      <small>{{ p.idade }} anos · {{ p.sintomas.join(', ') }}</small>
                    </td>
                    <td>@if (p.cor) { <app-selo [cor]="p.cor" /> }</td>
                    <td class="num">{{ p.pontuacao }}</td>
                    <td>
                      <div class="espera" [class]="'espera ' + situacaoEspera(p)">
                        <div class="linha">
                          <span>{{ formatarMinutos(minutosEsperando(p)) }}</span>
                          <span>{{ p.cor === 'VERMELHO' ? 'imediato' : formatarMinutos(meta(p)) }}</span>
                        </div>
                        <div class="trilho"><span [style.width.%]="porcentagemEspera(p)"></span></div>
                        @if (situacaoEspera(p) === 'estourou') {
                          <span class="aviso">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2 1 21h22L12 2zm1 15h-2v-2h2v2zm0-4h-2V9h2v4z" fill="currentColor"/></svg>
                            acima da meta
                          </span>
                        }
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr><td colspan="5" class="vazio">Ninguém aguardando atendimento</td></tr>
                }
                @if ((fila?.aguardando?.length ?? 0) > 6) {
                  <tr><td></td><td colspan="4" class="vazio">e mais {{ fila!.aguardando.length - 6 }} aguardando</td></tr>
                }
              </tbody>
            </table>
          </div>
        </div>

        <div class="bloco">
          <header><b>Consultórios livres</b><span class="estrutura">fila circular · vetor[{{ circular?.capacidade ?? 5 }}]</span></header>
          @if (circular) {
            <div class="vetor">
              @for (item of circular.vetor; track $index) {
                <span class="ptr">{{ circular.tamanho > 0 && $index === circular.inicio ? 'início' : '' }}</span>
              }
              @for (item of circular.vetor; track $index) {
                @if (item === 'vazio') {
                  <span class="cel vz">·</span>
                } @else {
                  <span class="cel" [class.ini]="$index === circular.inicio" [class.pulo]="estaFechado(item)">{{ item }}</span>
                }
              }
              @for (item of circular.vetor; track $index) {
                <span class="idx">[{{ $index }}]</span>
              }
              @for (item of circular.vetor; track $index) {
                <span class="ptr">{{ $index === circular.fim ? 'fim' : '' }}</span>
              }
            </div>
            <p class="legenda num">início = {{ circular.inicio }} · fim = {{ circular.fim }} · tamanho = {{ circular.tamanho }}/{{ circular.capacidade }}</p>
          }
          <span class="rot">Eventos</span>
          <ul class="log">
            @for (e of eventos.slice(0, 6); track $index) {
              <li><time>{{ e.substring(0, 8) }}</time><span>{{ e.substring(10) }}</span></li>
            } @empty {
              <li class="vazio">Nenhum evento ainda</li>
            }
          </ul>
        </div>
      </section>

      <div class="opbar">
        <div class="grupo">
          <button class="btn pri" (click)="triar()" [disabled]="!fila?.recepcao?.length">Triar próximo da recepção</button>
          <button class="btn" (click)="chamar()" [disabled]="!fila?.aguardando?.length">Chamar próximo</button>
        </div>
      </div>
    </div>
  `
})
export class PainelComponent implements OnInit, OnDestroy {
  consultorios: Consultorio[] = [];
  fila: EstadoFila | null = null;
  circular: FilaCircular | null = null;
  eventos: string[] = [];
  chamadas: Chamada[] = [];
  atendidos = 0;
  mensagem = '';
  erro = false;

  raio = RAIO;
  circunferencia = CIRCUNFERENCIA;
  abreviar = abreviar;
  horaMinuto = horaMinuto;
  formatarMinutos = formatarMinutos;

  private temporizador?: ReturnType<typeof setInterval>;
  private limparMensagem?: ReturnType<typeof setTimeout>;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.atualizar();
    this.temporizador = setInterval(() => this.atualizar(), 1000);
  }

  ngOnDestroy() {
    clearInterval(this.temporizador);
    clearTimeout(this.limparMensagem);
  }

  atualizar() {
    forkJoin({
      consultorios: this.upa.consultorios(),
      fila: this.upa.estadoFila(),
      circular: this.upa.filaCircular(),
      eventos: this.upa.eventos(),
      chamadas: this.upa.chamadas(),
      historico: this.upa.historico('inicio')
    }).subscribe({
      next: r => {
        this.consultorios = r.consultorios;
        this.fila = r.fila;
        this.circular = r.circular;
        this.eventos = r.eventos;
        this.chamadas = r.chamadas;
        this.atendidos = r.historico.length;
      },
      error: () => this.avisar('Não foi possível falar com o servidor', true)
    });
  }

  chamadaAtual(): Chamada[] {
    return this.chamadas.length ? [this.chamadas[0]] : [];
  }

  vez(): number | null {
    if (!this.circular || this.circular.tamanho === 0) {
      return null;
    }
    const item = this.circular.vetor[this.circular.inicio];
    return item === 'vazio' ? null : Number(item);
  }

  estaFechado(item: string): boolean {
    const sala = this.consultorios.find(c => c.numero === Number(item));
    return !!sala && !sala.aberto;
  }

  deslocamento(s: Consultorio): number {
    if (!s.duracaoSegundos || s.restamSegundos === null) {
      return 0;
    }
    return CIRCUNFERENCIA - CIRCUNFERENCIA * (s.restamSegundos / s.duracaoSegundos);
  }

  contarCor(cor: Cor): number {
    if (!this.fila) {
      return 0;
    }
    return [...this.fila.recepcao, ...this.fila.aguardando].filter(p => p.cor === cor).length;
  }

  // o atendimento simulado dura 15 a 40 s, então cada segundo de espera conta como 1 minuto
  minutosEsperando(p: Paciente): number {
    if (!p.chegadaEm) {
      return 0;
    }
    return Math.max(0, Math.floor((Date.now() - new Date(p.chegadaEm).getTime()) / 1000));
  }

  esperaMedia(): number {
    const lista = this.fila?.aguardando ?? [];
    if (!lista.length) {
      return 0;
    }
    const total = lista.reduce((soma, p) => soma + this.minutosEsperando(p), 0);
    return Math.round(total / lista.length);
  }

  meta(p: Paciente): number {
    return p.cor ? META_ESPERA[p.cor] : 0;
  }

  situacaoEspera(p: Paciente): string {
    if (p.cor === 'VERMELHO') {
      return 'imediato';
    }
    const espera = this.minutosEsperando(p);
    const meta = this.meta(p);
    if (espera > meta) {
      return 'estourou';
    }
    return espera >= meta * 0.8 ? 'perto' : '';
  }

  porcentagemEspera(p: Paciente): number {
    if (p.cor === 'VERMELHO') {
      return 100;
    }
    return Math.min(100, (this.minutosEsperando(p) / this.meta(p)) * 100);
  }

  triar() {
    this.executar(this.upa.triar());
  }

  chamar() {
    this.executar(this.upa.chamar());
  }

  concluir(numero: number) {
    this.executar(this.upa.concluir(numero));
  }

  fechar(numero: number) {
    this.executar(this.upa.fecharConsultorio(numero));
  }

  abrir(numero: number) {
    this.executar(this.upa.abrirConsultorio(numero));
  }

  private executar(requisicao: Observable<unknown>) {
    requisicao.subscribe({
      next: () => this.atualizar(),
      error: e => this.avisar(erroDaApi(e, 'Erro ao falar com o servidor'), true)
    });
  }

  private avisar(texto: string, erro: boolean) {
    this.mensagem = texto;
    this.erro = erro;
    clearTimeout(this.limparMensagem);
    this.limparMensagem = setTimeout(() => (this.mensagem = ''), 4000);
  }
}
