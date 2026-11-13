package com.upa.servico;

import com.upa.excecao.NaoEncontradoException;
import com.upa.estruturas.ListaSimples;
import com.upa.modelo.Sintoma;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SintomaService {

    private final ListaSimples<Sintoma> catalogo = new ListaSimples<>();

    public SintomaService() {
        adicionar(1, "Tiro", 100);
        adicionar(2, "Parada cardiorrespiratoria", 100);
        adicionar(3, "Infarto", 95);
        adicionar(4, "Facada", 90);
        adicionar(5, "AVC", 90);
        adicionar(6, "Hemorragia grave", 85);
        adicionar(7, "Queimadura grave", 80);
        adicionar(8, "Trauma craniano", 75);
        adicionar(9, "Fratura exposta", 70);
        adicionar(10, "Convulsao", 60);
        adicionar(11, "Dor no peito", 55);
        adicionar(12, "Fratura", 50);
        adicionar(13, "Crise de asma", 45);
        adicionar(14, "Corte profundo", 35);
        adicionar(15, "Febre alta", 30);
        adicionar(16, "Desidratacao", 25);
        adicionar(17, "Torcao", 10);
        adicionar(18, "Nausea", 8);
        adicionar(19, "Dor de garganta", 5);
        adicionar(20, "Tosse", 5);
        adicionar(21, "Dor de cabeca leve", 4);
        adicionar(22, "Resfriado", 3);
    }

    private void adicionar(int id, String nome, int pontos) {
        catalogo.inserirFim(new Sintoma(id, nome, pontos));
    }

    public List<Sintoma> listar() {
        return catalogo.percorrer();
    }

    public Sintoma buscar(int id) {
        Sintoma sintoma = catalogo.buscar(s -> s.id() == id);
        if (sintoma == null) {
            throw new NaoEncontradoException("Sintoma " + id + " nao existe no catalogo");
        }
        return sintoma;
    }
}
