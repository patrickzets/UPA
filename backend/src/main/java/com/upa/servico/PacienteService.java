package com.upa.servico;

import com.upa.dto.PacienteRequest;
import com.upa.estruturas.ListaSimples;
import com.upa.excecao.NaoEncontradoException;
import com.upa.excecao.RegraNegocioException;
import com.upa.modelo.Paciente;
import com.upa.modelo.StatusPaciente;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final ListaSimples<Paciente> pacientes = new ListaSimples<>();
    private final OperacoesService operacoes;

    public PacienteService(OperacoesService operacoes) {
        this.operacoes = operacoes;
    }

    public synchronized Paciente cadastrar(PacienteRequest dados) {
        String cpf = limparCpf(dados.cpf());
        if (cpf.length() != 11) {
            throw new RegraNegocioException("O CPF precisa ter 11 digitos");
        }
        if (pacientes.buscar(p -> p.getCpf().equals(cpf)) != null) {
            throw new RegraNegocioException("Ja existe um paciente com o CPF " + cpf);
        }
        Paciente paciente = new Paciente(cpf, dados.nome().trim(), dados.idade(), dados.descricao().trim());
        pacientes.inserirFim(paciente);
        operacoes.registrar("Cadastro do paciente " + paciente.getNome(), () -> desfazerCadastro(cpf));
        return paciente;
    }

    public synchronized Paciente buscar(String cpf) {
        String limpo = limparCpf(cpf);
        Paciente paciente = pacientes.buscar(p -> p.getCpf().equals(limpo));
        if (paciente == null) {
            throw new NaoEncontradoException("Paciente com CPF " + limpo + " nao encontrado");
        }
        return paciente;
    }

    public synchronized List<Paciente> listar() {
        return pacientes.percorrer();
    }

    public synchronized void remover(String cpf) {
        Paciente paciente = buscar(cpf);
        StatusPaciente status = paciente.getStatus();
        if (status != StatusPaciente.CADASTRADO && status != StatusPaciente.ATENDIDO) {
            throw new RegraNegocioException("Nao e possivel remover " + paciente.getNome()
                    + " enquanto ele esta em " + status);
        }
        pacientes.remover(p -> p.getCpf().equals(paciente.getCpf()));
    }

    private void desfazerCadastro(String cpf) {
        Paciente paciente = pacientes.buscar(p -> p.getCpf().equals(cpf));
        if (paciente == null) {
            throw new RegraNegocioException("O paciente ja foi removido");
        }
        if (paciente.getStatus() != StatusPaciente.CADASTRADO) {
            throw new RegraNegocioException("O paciente " + paciente.getNome() + " ja entrou no fluxo de atendimento");
        }
        pacientes.remover(p -> p.getCpf().equals(cpf));
    }

    private String limparCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }
}
