package com.upa;

import com.upa.dto.ChamadaDto;
import com.upa.dto.ChegadaRequest;
import com.upa.dto.PacienteRequest;
import com.upa.excecao.RegraNegocioException;
import com.upa.modelo.Cor;
import com.upa.modelo.StatusPaciente;
import com.upa.servico.AtendimentoService;
import com.upa.servico.ConsultorioService;
import com.upa.servico.OperacoesService;
import com.upa.servico.PacienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "upa.dados-ficticios=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FluxoUpaTest {

    @Autowired
    PacienteService pacienteService;
    @Autowired
    AtendimentoService atendimentoService;
    @Autowired
    ConsultorioService consultorioService;
    @Autowired
    OperacoesService operacoesService;

    private void novoPaciente(String cpf, String nome, Cor cor, Integer... sintomas) {
        pacienteService.cadastrar(new PacienteRequest(cpf, nome, 30, "teste"));
        atendimentoService.registrarChegada(new ChegadaRequest(cpf, cor, List.of(sintomas)));
        atendimentoService.triarProximo();
    }

    @Test
    void tiroPassaNaFrenteDeFraturaDentroDaMesmaCor() {
        novoPaciente("10000000001", "Fratura", Cor.VERMELHO, 12);
        novoPaciente("10000000002", "Tiro", Cor.VERMELHO, 1);
        novoPaciente("10000000003", "Resfriado", Cor.VERDE, 22);
        novoPaciente("10000000004", "Infarto leve", Cor.AMARELO, 11);

        assertEquals("Tiro", atendimentoService.chamarProximo().paciente().getNome());
        assertEquals("Fratura", atendimentoService.chamarProximo().paciente().getNome());
        assertEquals("Infarto leve", atendimentoService.chamarProximo().paciente().getNome());
        assertEquals("Resfriado", atendimentoService.chamarProximo().paciente().getNome());
    }

    @Test
    void consultorioFechadoEPuladoEPacienteEsperaQuandoNaoHaVaga() {
        for (int i = 1; i <= 7; i++) {
            novoPaciente("2000000000" + i, "Paciente " + i, Cor.VERDE, 22);
        }
        consultorioService.fechar(2, true);

        ChamadaDto primeira = atendimentoService.chamarProximo();
        ChamadaDto segunda = atendimentoService.chamarProximo();
        assertEquals(1, primeira.consultorio());
        assertEquals(3, segunda.consultorio());
        atendimentoService.chamarProximo();
        atendimentoService.chamarProximo();
        assertThrows(RegraNegocioException.class, () -> atendimentoService.chamarProximo());

        atendimentoService.concluir(3);
        assertEquals(3, atendimentoService.chamarProximo().consultorio());
    }

    @Test
    void naoPermiteChamarQuemJaFoiAtendidoEDesfazCadastro() {
        pacienteService.cadastrar(new PacienteRequest("30000000001", "Teste", 20, "teste"));
        operacoesService.desfazer();
        assertTrue(pacienteService.listar().stream().noneMatch(p -> p.getCpf().equals("30000000001")));

        novoPaciente("30000000002", "Atendido", Cor.VERDE, 22);
        int consultorio = atendimentoService.chamarProximo().consultorio();
        atendimentoService.concluir(consultorio);
        assertEquals(StatusPaciente.ATENDIDO, pacienteService.buscar("30000000002").getStatus());
        assertThrows(RuntimeException.class, () -> atendimentoService.concluir(consultorio));
    }
}
