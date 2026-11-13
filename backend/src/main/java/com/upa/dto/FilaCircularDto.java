package com.upa.dto;

import java.util.List;

public record FilaCircularDto(List<String> vetor, int inicio, int fim, int tamanho, int capacidade) {
}
