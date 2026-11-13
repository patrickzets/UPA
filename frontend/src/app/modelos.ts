export type Cor = 'VERMELHO' | 'AMARELO' | 'VERDE';

export interface Paciente {
  cpf: string;
  nome: string;
  idade: number;
  descricao: string;
  status: string;
  cor: Cor | null;
  pontuacao: number;
  sintomas: string[];
}

export interface Sintoma {
  id: number;
  nome: string;
  pontos: number;
}

export interface Consultorio {
  numero: number;
  aberto: boolean;
  ocupado: boolean;
  naFila: boolean;
  paciente: string | null;
  reabreEmSegundos: number | null;
}

export interface EstadoFila {
  recepcao: Paciente[];
  aguardando: Paciente[];
  proximo: Paciente | null;
  emAtendimento: Paciente[];
}

export interface FilaCircular {
  vetor: string[];
  inicio: number;
  fim: number;
  tamanho: number;
  capacidade: number;
}

export interface Agendamento {
  id: number;
  cpf: string;
  data: string;
  horario: string;
  especialidade: string;
}

export interface Atendimento {
  id: number;
  nomePaciente: string;
  cor: Cor;
  pontuacao: number;
  consultorio: number;
  inicio: string;
  fim: string;
}

export interface Operacao {
  descricao: string;
  hora: string;
}

export interface Configuracao {
  intervaloFechamentoSeg: number;
  duracaoFechamentoSeg: number;
  fechamentoAutomatico: boolean;
}
