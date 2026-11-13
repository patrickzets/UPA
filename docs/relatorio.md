# Relatório Técnico

Estrutura de Dados I, Sistema de Atendimento da UPA.

## 1. Estruturas implementadas

Todas estão em `com.upa.estruturas`. Nas estruturas encadeadas usamos nós e referências. Os métodos que devolvem `List` (percorrer, exibir) usam `ArrayList` só para entregar o resultado à API, sem substituir a estrutura.

| Estrutura | Onde é usada | Operações |
| --- | --- | --- |
| Lista simplesmente encadeada | cadastro de pacientes e catálogo de sintomas | inserirInicio, inserirFim, buscar, remover, percorrer |
| Lista duplamente encadeada | histórico de atendimentos | inserirInicio, inserirFim, remover, percorrerInicioFim, percorrerFimInicio, navegação por nós |
| Lista ordenada | agenda de consultas | inserir mantendo a ordem, buscar, remover, exibir |
| Pilha | operações que podem ser desfeitas | push, pop, peek, isEmpty |
| Fila simples | recepção (ordem de chegada antes da triagem) | enqueue, dequeue, peek, isEmpty |
| Fila circular | consultórios livres | enqueue, dequeue, peek, isEmpty, isFull |
| Fila com prioridade | pacientes aguardando atendimento | inserir, proximo, retirar |

## 2. Finalidade, justificativa, vantagens e limitações

**Lista simples (pacientes).** O cadastro cresce e diminui em qualquer posição e a busca é sempre por CPF, então não precisa de acesso por índice. Vantagem: inserir no início e remover sem deslocar elementos. Limitação: buscar é linear e inserir no fim também, porque não guardamos referência para o último nó.

**Lista dupla (histórico).** Pede navegação nos dois sentidos. Cada nó guarda o anterior e o próximo, e o serviço mantém um cursor que anda para frente e para trás. Vantagem: voltar sem refazer o percurso desde o início e inserir no fim em tempo constante. Limitação: cada nó ocupa mais memória por causa da referência extra.

**Lista ordenada (agenda).** As consultas chegam fora de ordem e precisam ficar sempre organizadas por data e horário. A inserção já procura a posição certa, então não é preciso ordenar depois. O critério de desempate é o id da consulta. Limitação: inserir custa O(n).

**Pilha (desfazer).** A última operação feita é a primeira a ser desfeita, que é exatamente o comportamento LIFO. Cada operação guarda a descrição e a ação que a reverte (remover o paciente cadastrado ou o agendamento criado). Só é permitido desfazer um cadastro se o paciente ainda não entrou no fluxo de atendimento, para não deixar o sistema inconsistente.

**Fila simples (recepção).** Quem chega entra no fim e é triado na ordem de chegada. Só depois da triagem o paciente entra na fila com prioridade. Limitação: não permite furar fila, o que é desejado nesse ponto.

**Fila circular (consultórios).** O vetor tem tamanho 5 (um lugar por consultório) e as posições liberadas são reaproveitadas com `(posicao + 1) % tamanho`. Guarda o número dos consultórios livres na ordem em que ficaram livres. Vantagem: sem alocação e operações em tempo constante. Limitação: capacidade fixa. Se estiver cheia, `enqueue` lança `FilaCheiaException`, mas no sistema isso não acontece porque nunca há mais consultórios que posições.

**Fila com prioridade (espera).** Implementada com heap binário em vetor. A comparação usa, nesta ordem, a cor (vermelho, amarelo, verde), a pontuação (maior primeiro) e o número de chegada, que preserva a ordem entre empatados. Vantagem: inserir e retirar em O(log n), contra O(n) de uma lista ordenada. Limitação: listar toda a fila em ordem exige copiar o heap e retirar elemento por elemento.

## 3. Complexidade

n é o número de elementos na estrutura.

| Estrutura | Operação | Complexidade |
| --- | --- | --- |
| Lista simples | inserirInicio | O(1) |
| | inserirFim, buscar, remover, percorrer | O(n) |
| Lista dupla | inserirInicio, inserirFim | O(1) |
| | buscar, remover (por critério), percorrer | O(n) |
| | navegar para o próximo ou anterior | O(1) |
| Lista ordenada | inserir, buscar, remover, exibir | O(n) |
| Pilha | push, pop, peek, isEmpty | O(1) |
| Fila simples | enqueue, dequeue, peek, isEmpty | O(1) |
| Fila circular | enqueue, dequeue, peek, isEmpty, isFull | O(1) |
| Fila com prioridade | inserir, retirar | O(log n) |
| | proximo | O(1) |
| | listar em ordem | O(n log n) |

## 4. Regras do sistema

**Pontuação.** Cada sintoma do catálogo tem uma pontuação (tiro 100, facada 90, fratura 50, dor de garganta 5). A pontuação do paciente é a soma dos sintomas marcados na chegada. Dentro da mesma cor, quem tem mais pontos é chamado primeiro, então um paciente vermelho com tiro passa na frente de um vermelho com fratura. Entre pacientes com cor e pontuação iguais vale a ordem de chegada.

**Distribuição entre consultórios.** O sistema tem 5 consultórios. Ao chamar um paciente, tira-se o primeiro consultório da fila circular. Se ele estiver fechado, é descartado da fila e tenta-se o próximo. Se nenhum servir, o paciente continua aguardando e a chamada devolve um aviso. Quando um consultório conclui o atendimento ou reabre, entra no fim da fila. Assim a fila de espera é única e vai sempre para quem liberar primeiro.

**Fechamento aleatório.** Um agendador roda a cada segundo. Passado o intervalo configurado, sorteia um consultório aberto e o fecha por um tempo configurado. Se ele estava atendendo, termina o atendimento antes de ficar fora de operação. Nunca fecha o último consultório aberto.

**Consistência.** Um paciente só entra na fila se estiver cadastrado ou já atendido, só é chamado se estiver aguardando e só pode ser removido do cadastro fora do fluxo de atendimento. Concluir um consultório que não está atendendo ninguém é recusado.

**Tratamento de estruturas vazias.** `EstruturaVaziaException` (pilha, filas, histórico) e `FilaCheiaException` viram respostas 409 com mensagem, e no console são impressas no menu sem encerrar o programa.

## 5. Cobertura dos testes obrigatórios

| Teste pedido | Onde ver |
| --- | --- |
| Cadastro de cinco pacientes | dados fictícios (oito pacientes) |
| Diferentes níveis de prioridade | fila com vermelho, amarelo e verde |
| Atendimento por prioridade e chegada | `FluxoUpaTest.tiroPassaNaFrenteDeFraturaDentroDaMesmaCor` e `EstruturasTest.filaPrioridadeRespeitaPrioridadeEChegada` |
| Agendamentos fora de ordem | `EstruturasTest.listaOrdenadaMantemOrdemComInsercaoDesordenada` e agenda de exemplo |
| Histórico nos dois sentidos | `EstruturasTest.listaDuplaPercorreNosDoisSentidos` e `/api/historico` |
| Desfazer com a pilha | `FluxoUpaTest.naoPermiteChamarQuemJaFoiAtendidoEDesfazCadastro` |
| Fila circular com reaproveitamento | `EstruturasTest.filaCircularReaproveitaPosicoesETrataCheia` |
| Estruturas vazias | testes de pilha, fila e fila circular que esperam a exceção |
