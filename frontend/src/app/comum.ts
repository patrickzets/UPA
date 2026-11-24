import { Component, Input } from '@angular/core';
import { Cor } from './modelos';

export const NOME_COR: Record<Cor, string> = {
  VERMELHO: 'Emergência',
  AMARELO: 'Urgente',
  VERDE: 'Pouco urgente'
};

// meta de espera em minutos, como no protocolo de Manchester (vermelho é imediato)
export const META_ESPERA: Record<Cor, number> = { VERMELHO: 0, AMARELO: 60, VERDE: 120 };

const PARTICULAS = ['de', 'da', 'do', 'dos', 'das', 'e'];
const SUFIXOS = ['Filho', 'Neto', 'Júnior', 'Junior', 'Sobrinho'];

// "Maria Aparecida dos Santos Pereira Lima" -> "Maria A. S. P. Lima"
export function abreviar(nome: string, max = 24): string {
  if (!nome || nome.length <= max) {
    return nome;
  }
  const partes = nome.split(' ');
  let fim = partes.length - 1;
  let ultimo = partes[fim];
  if (SUFIXOS.includes(ultimo) && fim > 1) {
    fim--;
    ultimo = partes[fim] + ' ' + ultimo;
  }
  const iniciais = partes.slice(1, fim)
    .filter(p => !PARTICULAS.includes(p.toLowerCase()))
    .map(p => p[0].toUpperCase() + '.')
    .join(' ');
  return [partes[0], iniciais, ultimo].filter(p => p).join(' ');
}

export function horaMinuto(dataHora: string | null): string {
  return dataHora ? dataHora.substring(11, 16) : '';
}

export function formatarMinutos(min: number): string {
  if (min < 60) {
    return `${min} min`;
  }
  return `${Math.floor(min / 60)}h${String(min % 60).padStart(2, '0')}`;
}

export function erroDaApi(e: any, padrao: string): string {
  return e?.error?.erro ?? padrao;
}

@Component({
  selector: 'app-selo',
  standalone: true,
  template: `
    <span class="selo" [class]="'selo ' + cor">
      @if (cor === 'VERMELHO') {
        <svg viewBox="0 0 12 12" aria-hidden="true"><path d="M6 .8 11.2 6 6 11.2.8 6Z" fill="currentColor"/></svg>
      } @else if (cor === 'AMARELO') {
        <svg viewBox="0 0 12 12" aria-hidden="true"><path d="M6 1 11.4 10.6H.6Z" fill="currentColor"/></svg>
      } @else {
        <svg viewBox="0 0 12 12" aria-hidden="true"><circle cx="6" cy="6" r="5" fill="currentColor"/></svg>
      }
      {{ nomes[cor] }}
    </span>
  `
})
export class SeloComponent {
  @Input({ required: true }) cor!: Cor;
  nomes = NOME_COR;
}
