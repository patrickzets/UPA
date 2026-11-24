# Sistema de Atendimento da UPA

Trabalho prático de Estrutura de Dados I (Engenharia de Software, 3º período).
Simulação acadêmica de uma Unidade de Pronto Atendimento. As cores e pontuações são fictícias e não substituem nenhum protocolo real de classificação de risco.

## Estrutura do projeto

```
sistema de UPA/
  backend/    Spring Boot 3 (Java 21), API REST e menu de console
  frontend/   Angular 18, painel do operador, telão da sala de espera e telas de cadastro
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

## Como rodar o front

Com o backend rodando:

```bash
cd frontend
npm install
npm start
```

Abre em `http://localhost:4200` e fala com a API na porta 8080.

### Telas

| Rota | Tela | O que mostra |
| --- | --- | --- |
| `/` | Painel | chamada atual, os 5 consultórios, recepção, fila de prioridade, vetor da fila circular e eventos |
| `/telao` | Telão | tela para a TV da sala de espera, com a chamada atual, as chamadas anteriores e a situação dos consultórios |
| `/pacientes` | Pacientes | cadastro, registro de chegada (cor e sintomas) e lista de pacientes |
| `/agenda` | Agenda | consultas marcadas, sempre em ordem de data e horário |
| `/historico` | Histórico | atendimentos concluídos, percorridos para frente e para trás |
| `/configuracoes` | Configurações | fechamento aleatório dos consultórios |

O painel e o telão se atualizam sozinhos a cada segundo.

No painel, cada paciente na fila mostra há quanto tempo está esperando e a meta da sua cor (vermelho imediato, amarelo 60 min, verde 120 min). Como o atendimento simulado dura poucos segundos, 1 segundo de espera conta como 1 minuto.

O telão mostra o nome completo do paciente e a senha (nomes muito longos viram, por exemplo, "Maria A. S. P. Lima"). Quando alguém é chamado, o cartão fica azul por alguns segundos e pode tocar um aviso sonoro. Se dois consultórios liberam ao mesmo tempo, as chamadas entram numa fila e cada uma fica pelo menos 8 segundos na tela, para uma não atropelar a outra. O botão "Tela cheia" deixa o telão ocupando a TV inteira.

## Como o atendimento funciona

1. O paciente é cadastrado (CPF, nome, idade, motivo da procura).
2. Na chegada, escolhe-se a cor e marcam-se os sintomas. Cada sintoma tem uma pontuação (tiro vale mais que fratura, por exemplo) e a pontuação do paciente é a soma deles. Nesse momento ele recebe uma senha (U001, U002...).
3. Ele entra na fila da recepção (fila simples) e depois passa pela triagem, que o coloca na fila com prioridade.
4. A ordem de chamada é: cor (vermelho, amarelo, verde), depois maior pontuação, depois ordem de chegada.
5. O paciente vai para o próximo consultório livre da fila circular. Se nenhum estiver livre, ele continua aguardando.

O atendimento é automático: a cada segundo o sistema conclui os atendimentos que já terminaram (cada um dura de 15 a 40 segundos) e chama os próximos da fila enquanto houver consultório livre. Os botões de chamar e concluir no painel continuam funcionando para fazer isso na mão.

### Consultórios

São 5 consultórios. Quando um termina o atendimento, entra no fim da fila circular. Quem fecha é pulado e o próximo livre assume. Ao reabrir, volta para a fila.

Na tela de configurações dá para ligar ou desligar o fechamento aleatório, definir de quanto em quanto tempo um consultório é sorteado para fechar e por quanto tempo ele fica fechado. Sempre fica pelo menos um aberto.

## Endpoints principais

| Método | Rota | O que faz |
| --- | --- | --- |
| GET, POST | `/api/pacientes` | lista e cadastra |
| GET, DELETE | `/api/pacientes/{cpf}` | consulta e remove |
| GET | `/api/sintomas` | catálogo com pontuação |
| POST | `/api/fila/chegada` | registra chegada com cor e sintomas e gera a senha |
| POST | `/api/fila/triar` | passa o primeiro da recepção para a fila de prioridade |
| GET | `/api/fila` | recepção, aguardando e em atendimento |
| GET | `/api/fila/proximo` | próximo a ser chamado |
| GET | `/api/fila/chamadas` | últimas chamadas, da mais recente para a mais antiga |
| POST | `/api/fila/chamar` | chama o próximo para um consultório |
| POST | `/api/fila/concluir/{consultorio}` | conclui o atendimento |
| GET | `/api/consultorios` | situação dos consultórios, com paciente, senha e tempo restante |
| GET | `/api/consultorios/fila-circular` | vetor da fila circular |
| GET | `/api/consultorios/eventos` | fechamentos e reaberturas dos consultórios |
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
- Pacientes nas três cores. Como o atendimento é automático, eles são chamados logo que o sistema sobe: no histórico, o tiro (Bruno, 100 pts) aparece antes da fratura (Ana, 50 pts), ambos vermelhos.
- Para ver a fila de prioridade cheia, registre a chegada de vários pacientes na tela de Pacientes e acompanhe no painel a ordem por cor e pontos.
- Atendimentos concluídos no histórico, que pode ser percorrido nos dois sentidos.
- Agenda inserida fora de ordem e exibida ordenada.
- Desfazer: cadastrar um paciente e usar "Desfazer última operação" na tela de Pacientes.
- Fila circular: o painel mostra o vetor com os ponteiros de início e fim girando conforme os consultórios são ocupados e liberados. Um consultório que fecha aparece riscado e é pulado.
- Estruturas vazias: chamar sem ninguém na fila, desfazer sem operações e navegar em histórico vazio devolvem mensagem de erro tratada, sem quebrar.

Relatório técnico em [docs/relatorio.md](docs/relatorio.md).
