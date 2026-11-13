import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Agendamento, Atendimento, Configuracao, Consultorio, EstadoFila, FilaCircular,
  Operacao, Paciente, Sintoma
} from './modelos';

@Injectable({ providedIn: 'root' })
export class UpaService {
  private api = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  pacientes(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.api}/pacientes`);
  }

  cadastrarPaciente(dados: { cpf: string; nome: string; idade: number; descricao: string }) {
    return this.http.post<Paciente>(`${this.api}/pacientes`, dados);
  }

  removerPaciente(cpf: string) {
    return this.http.delete(`${this.api}/pacientes/${cpf}`);
  }

  sintomas(): Observable<Sintoma[]> {
    return this.http.get<Sintoma[]>(`${this.api}/sintomas`);
  }

  registrarChegada(dados: { cpf: string; cor: string; sintomas: number[] }) {
    return this.http.post<Paciente>(`${this.api}/fila/chegada`, dados);
  }

  triar() {
    return this.http.post<Paciente>(`${this.api}/fila/triar`, {});
  }

  estadoFila(): Observable<EstadoFila> {
    return this.http.get<EstadoFila>(`${this.api}/fila`);
  }

  chamar() {
    return this.http.post(`${this.api}/fila/chamar`, {});
  }

  concluir(consultorio: number) {
    return this.http.post(`${this.api}/fila/concluir/${consultorio}`, {});
  }

  consultorios(): Observable<Consultorio[]> {
    return this.http.get<Consultorio[]>(`${this.api}/consultorios`);
  }

  filaCircular(): Observable<FilaCircular> {
    return this.http.get<FilaCircular>(`${this.api}/consultorios/fila-circular`);
  }

  eventos(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/consultorios/eventos`);
  }

  fecharConsultorio(numero: number) {
    return this.http.post(`${this.api}/consultorios/${numero}/fechar`, {});
  }

  abrirConsultorio(numero: number) {
    return this.http.post(`${this.api}/consultorios/${numero}/abrir`, {});
  }

  agenda(): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(`${this.api}/agendamentos`);
  }

  agendar(dados: { cpf: string; data: string; horario: string; especialidade: string }) {
    return this.http.post<Agendamento>(`${this.api}/agendamentos`, dados);
  }

  removerAgendamento(id: number) {
    return this.http.delete(`${this.api}/agendamentos/${id}`);
  }

  historico(ordem: 'inicio' | 'fim'): Observable<Atendimento[]> {
    return this.http.get<Atendimento[]>(`${this.api}/historico?ordem=${ordem}`);
  }

  navegarHistorico(destino: 'primeiro' | 'ultimo' | 'proximo' | 'anterior'): Observable<Atendimento> {
    return this.http.get<Atendimento>(`${this.api}/historico/${destino}`);
  }

  operacoes(): Observable<Operacao[]> {
    return this.http.get<Operacao[]>(`${this.api}/operacoes`);
  }

  desfazer() {
    return this.http.post<{ mensagem: string }>(`${this.api}/operacoes/desfazer`, {});
  }

  configuracao(): Observable<Configuracao> {
    return this.http.get<Configuracao>(`${this.api}/configuracao`);
  }

  salvarConfiguracao(dados: Configuracao) {
    return this.http.put<Configuracao>(`${this.api}/configuracao`, dados);
  }
}
