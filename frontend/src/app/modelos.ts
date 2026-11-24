export type Cor = 'VERMELHO' | 'AMARELO' | 'VERDE';

export type StatusPaciente = 'CADASTRADO' | 'NA_RECEPCAO' | 'AGUARDANDO' | 'EM_ATENDIMENTO' | 'ATENDIDO';

export interface Paciente {
  cpf: string;
  nome: string;
  idade: number;
  descricao: string;
  status: StatusPaciente;
  cor: Cor | null;
  pontuacao: number;
  sintomas: string[];
  chegadaEm: string | null;
  senha: string | null;
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
  cpfPaciente: string | null;
  inicioAtendimento: string | null;
  reabreEmSegundos: number | null;
  cor: Cor | null;
  senha: string | null;
  duracaoSegundos: number | null;
  restamSegundos: number | null;
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

export interface Chamada {
  id: number;
  nome: string;
  senha: string;
  cor: Cor;
  consultorio: number;
  hora: string;
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
