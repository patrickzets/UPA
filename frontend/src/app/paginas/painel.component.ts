import { Component, OnDestroy, OnInit } from '@angular/core';
import { Consultorio, EstadoFila, FilaCircular } from '../modelos';
import { UpaService } from '../upa.service';

@Component({
  selector: 'app-painel',
  standalone: true,
  template: `
    @if (erro) {
      <div class="erro">{{ erro }}</div>
    }

    <h2>Consultorios</h2>
    <div class="grade">
      @for (c of consultorios; track c.numero) {
        <div class="consultorio" [class.fechado]="!c.aberto" [class.ocupado]="c.ocupado">
          <b>Consultorio {{ c.numero }}</b><br>
          @if (!c.aberto) {
            Fechado
            @if (c.reabreEmSegundos !== null) { (volta em {{ c.reabreEmSegundos }}s) }
            <br><button (click)="abrir(c.numero)">Abrir</button>
          } @else if (c.ocupado) {
            Atendendo {{ c.paciente }}<br>
            <button (click)="concluir(c.numero)">Concluir</button>
            <button (click)="fechar(c.numero)">Fechar</button>
          } @else {
            Livre<br>
            <button (click)="fechar(c.numero)">Fechar</button>
          }
        </div>
      }
    </div>

    <div class="caixa" style="margin-top: 16px">
      <h3>Fila circular de consultorios livres</h3>
      @if (circular) {
        <div class="vetor">
          @for (item of circular.vetor; track $index) {
            <span>{{ item }}</span>
          }
        </div>
        <small>inicio = {{ circular.inicio }} | fim = {{ circular.fim }} | tamanho = {{ circular.tamanho }}</small>
      }
    </div>

    <div class="caixa">
      <h3>Proximo a ser chamado</h3>
      @if (fila?.proximo) {
        <span [class]="fila!.proximo!.cor">{{ fila!.proximo!.cor }}</span>
        {{ fila!.proximo!.nome }} ({{ fila!.proximo!.pontuacao }} pts)
      } @else {
        Ninguem aguardando.
      }
      <br>
      <button (click)="chamar()">Chamar proximo</button>
      <button (click)="triar()">Triar proximo da recepcao</button>
    </div>

    <div class="caixa">
      <h3>Recepcao ({{ fila?.recepcao?.length ?? 0 }})</h3>
      @for (p of fila?.recepcao; track p.cpf) {
        <div><span [class]="p.cor">{{ p.cor }}</span> {{ p.nome }} | {{ p.pontuacao }} pts</div>
      }
    </div>

    <div class="caixa">
      <h3>Aguardando atendimento ({{ fila?.aguardando?.length ?? 0 }})</h3>
      <table>
        <tr><th>Cor</th><th>Paciente</th><th>Pontos</th><th>Motivo</th></tr>
        @for (p of fila?.aguardando; track p.cpf) {
          <tr>
            <td [class]="p.cor">{{ p.cor }}</td>
            <td>{{ p.nome }}</td>
            <td>{{ p.pontuacao }}</td>
            <td>{{ p.sintomas.join(', ') }}</td>
          </tr>
        }
      </table>
    </div>

    <div class="caixa">
      <h3>Eventos dos consultorios</h3>
      @for (e of eventos; track $index) {
        <div>{{ e }}</div>
      }
    </div>
  `
})
export class PainelComponent implements OnInit, OnDestroy {
  consultorios: Consultorio[] = [];
  fila: EstadoFila | null = null;
  circular: FilaCircular | null = null;
  eventos: string[] = [];
  erro = '';
  private temporizador?: ReturnType<typeof setInterval>;

  constructor(private upa: UpaService) {}

  ngOnInit() {
    this.atualizar();
    this.temporizador = setInterval(() => this.atualizar(), 2000);
  }

  ngOnDestroy() {
    clearInterval(this.temporizador);
  }

  atualizar() {
    this.upa.consultorios().subscribe(c => (this.consultorios = c));
    this.upa.estadoFila().subscribe(f => (this.fila = f));
    this.upa.filaCircular().subscribe(f => (this.circular = f));
    this.upa.eventos().subscribe(e => (this.eventos = e));
  }

  chamar() {
    this.executar(this.upa.chamar());
  }

  triar() {
    this.executar(this.upa.triar());
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

  private executar(chamada: { subscribe: Function }) {
    this.erro = '';
    chamada.subscribe({
      next: () => this.atualizar(),
      error: (e: any) => (this.erro = e.error?.erro ?? 'Erro ao falar com o servidor')
    });
  }
}
