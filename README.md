# Sistema de Atendimento da UPA

Trabalho prático de Estrutura de Dados I (Engenharia de Software, 3º período).
Simulação acadêmica de uma Unidade de Pronto Atendimento. As cores e pontuações são fictícias e não substituem nenhum protocolo real de classificação de risco.

## Estrutura do projeto

```
sistema de UPA/
  backend/    Spring Boot 3 (Java 21), API REST e menu de console
  frontend/   esboço em Angular 18
  docs/       relatório técnico
```

As sete estruturas de dados ficam em `backend/src/main/java/com/upa/estruturas` e foram feitas à mão, sem usar `LinkedList`, `Stack`, `Queue`, `ArrayDeque` ou `PriorityQueue`.

## Requisitos

- Java 21
- Maven não precisa instalar, o projeto usa o Maven Wrapper (mvnw.cmd no Windows, ./mvnw no Linux e Mac)
- Node 18 ou superior (só para o front)

## Como rodar o backend (API)

```bash
cd backend
.\mvnw.cmd package -DskipTests
java -jar target\sistema-upa-1.0.0.jar
```

O `mvnw spring-boot:run` falha quando o caminho da pasta tem acento (como em "Programação"), por isso o jeito acima.

A API sobe em `http://localhost:8080`. Os dados fictícios são carregados sozinhos na inicialização (dá para desligar com `upa.dados-ficticios=false` no `application.properties`).

## Como rodar o menu de console

O enunciado pede um menu no terminal, então o mesmo sistema também roda sem web:

```bash
cd backend
.\mvnw.cmd package -DskipTests
java -jar target/sistema-upa-1.0.0.jar --spring.profiles.active=console
```

## Como rodar os testes

```bash
cd backend
.\mvnw.cmd test
```

São 10 testes: 7 das estruturas e 3 do fluxo da UPA (prioridade por pontos, consultório fechado sendo pulado e desfazer cadastro).

## Como rodar o front (esboço)

```bash
cd frontend
npm install
npm start
```

Abre em `http://localhost:4200` e fala com a API na porta 8080.

## Como o atendimento funciona

1. O paciente é cadastrado (CPF, nome, idade, solicitação).
2. Na chegada, escolhe-se a cor e marcam-se os sintomas. Cada sintoma tem uma pontuação (tiro vale mais que fratura, por exemplo) e a pontuação do paciente é a soma deles.
3. Ele entra na fila da recepção (fila simples) e depois passa pela triagem, que o coloca na fila com prioridade.
4. A ordem de chamada é: cor (vermelho, amarelo, verde), depois maior pontuação, depois ordem de chegada.
5. Ao chamar, o paciente vai para o próximo consultório livre da fila circular. Se nenhum estiver livre, ele continua aguardando.

### Consultórios

São 5 consultórios. Quando um termina o atendimento, entra no fim da fila circular. Quem fecha é pulado e o próximo livre assume. Ao reabrir, volta para a fila.

Na tela de configurações dá para ligar ou desligar o fechamento aleatório, definir de quanto em quanto tempo um consultório é sorteado para fechar e por quanto tempo ele fica fechado. Sempre fica pelo menos um aberto.

## Endpoints principais

| Método | Rota | O que faz |
| --- | --- | --- |
| GET, POST | `/api/pacientes` | lista e cadastra |
| GET, DELETE | `/api/pacientes/{cpf}` | consulta e remove |
| GET | `/api/sintomas` | catálogo com pontuação |
| POST | `/api/fila/chegada` | registra chegada com cor e sintomas |
| POST | `/api/fila/triar` | passa o primeiro da recepção para a fila de prioridade |
| GET | `/api/fila` | recepção, aguardando e em atendimento |
| GET | `/api/fila/proximo` | próximo a ser chamado |
| POST | `/api/fila/chamar` | chama o próximo para um consultório |
| POST | `/api/fila/concluir/{consultorio}` | conclui o atendimento |
| GET | `/api/consultorios` | situação dos consultórios |
| GET | `/api/consultorios/fila-circular` | vetor da fila circular |
| POST | `/api/consultorios/{n}/fechar` e `/abrir` | fecha e abre manualmente |
| GET, POST | `/api/agendamentos` | agenda ordenada |
| GET | `/api/historico` | `?ordem=inicio` ou `?ordem=fim` |
| GET | `/api/historico/primeiro`, `ultimo`, `proximo`, `anterior` | navegação |
| GET | `/api/operacoes` | últimas operações |
| POST | `/api/operacoes/desfazer` | desfaz o último cadastro ou agendamento |
| GET, PUT | `/api/configuracao` | fechamento aleatório |

## Roteiro de testes do enunciado

Os dados fictícios já deixam tudo pronto para demonstrar:

- Cinco ou mais pacientes cadastrados (são oito).
- Pacientes nas três cores. Na fila aparece o tiro (100 pts) na frente da fratura (50 pts), ambos vermelhos.
- Duas pessoas já atendidas, então o histórico pode ser percorrido nos dois sentidos.
- Agenda inserida fora de ordem e exibida ordenada.
- Desfazer: cadastrar um paciente e chamar `/api/operacoes/desfazer`.
- Fila circular: `/api/consultorios/fila-circular` mostra o vetor com as posições reaproveitadas. Chame e conclua atendimentos e acompanhe início e fim girando.
- Estruturas vazias: chamar sem ninguém na fila, desfazer sem operações e navegar em histórico vazio devolvem mensagem de erro tratada, sem quebrar.

Relatório técnico em [docs/relatorio.md](docs/relatorio.md).
