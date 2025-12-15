
# Jantar dos Filósofos - Prova de Programação Paralela e Distribuída

## Estrutura do Projeto

```
prova-jantar-filósofos/
├── README.md
├── RELATORIO.md
├── instruções.txt
└── src/
    ├── tarefa 1/    # Implementação básica com deadlock
    ├── tarefa 2/    # Soluç Ao com ordem diferente
    ├── tarefa 3/    # Solução com semáforos
    └── tarefa 4/    # Solução com monitores
```

## Instruções de Compilação e Execução

### Compilação

Para compilar cada tarefa, navegue até o diretório da tarefa e execute:

```bash
cd src/tarefa 1
javac *.java
```

Repita o processo para as outras tarefas (tarefa 2, tarefa 3, tarefa 4).

### Execucao

#### Tarefa 1: Implementação Básica com Deadlock
```bash
cd src/tarefa1
java Main
```
Executa por 30 segundos e demonstra o problema de deadlock.

#### Tarefa 2: Prevenção de Deadlock
```bash
cd src/tarefa2
java Main
```
Execute por 2 minutos sem deadlock, usando ordem diferente de pegar garfos.

#### Tarefa 3: Solução com Semáforos
```bash
cd src/tarefa3
java Main
```
Executa por 2 minutos usando semáforos para limitar a 4 o número de filósofos que podem tentar comer simultaneamente.

#### Tarefa 4: Solução com Monitores
```bash
cd src/tarefa4
java Main
```
Execute por 2 minutos usando monitor centralizado com garantia de fairness.

## Descrição das Soluções

### Tarefa 1: Implementação Básica com Deadlock

Esta implementação demonstra o problema clássico de deadlock no Jantar dos Filósofos.

**Por que ocorre deadlock?**

O deadlock ocorre quando todos os filósofos pegam o garfo esquerdo simultaneamente. Como cada filósofo precisa de dois garfos para comer, todos ficam esperando pelo garfo direito, que está sendo segurado pelo filósofo ao lado. Isso cria uma espira circular onde nenhum filósofo consegue prosseguir.

**Características:**
- Cada filósofo pega primeiro o garfo esquerdo, depois o direito
- Usa `synchronized` para exclusão mútua
- Sistema de logging completo
- Execucao por 30 segundos

### Tarefa 2: Prevenção de Deadlock

Esta solução previne deadlock fazendo com que um filósofo (ID 4) pegue os garfos em ordem inversa.

**Por que previne deadlock?**

Ao fazer o filósofo 4 pegar primeiro o garfo direito e depois o esquerdo, quebramos a espera circular. Se todos os outros filósofos pegarem o garfo esquerdo, o filósofo 4 pegará o primeiro direito, permitindo que pelo menos um filósofo complete sua refeição e libere os garfos.

**Starvation:**

Ainda existe possibilidade de starvation, pois não há garantia de que todos os filósofos terão oportunidades iguais de comer. Um filósofo pode ser continuamente preterido se os vizinhos sempre pegarem os garfos antes dele.

**Características:**
- Filosofo 4 pega garfos em ordem inversa
- Previne deadlock
- Sistema de estatísticas
- Execucao por 2 minutos

### Tarefa 3: Solução com Semáforos

Esta solução usa um semáforo para limitar a 4 o número de filósofos que podem tentar pegar garfos simultaneamente.

**Como funciona:**

Um semáforo global com 4 permissões garante que no máximo 4 filósofos tentam pegar garfos ao mesmo tempo. Como há 5 filósofos e 5 garfos, sempre haverá pelo menos um filósofo que conseguira pegar ambos os garfos.

**Por que previne deadlock?**

Com apenas 4 filósofos tentando simultaneamente, sempre haverá pelo menos um garfo disponível para um dos filósofos completar sua refeição, quebrando qualquer possível ciclo de espera.

**Vantagens:**
- Simples de implementar
- Previne deadlock de forma eficiente
- Boa performance

**Desvantagens:**
- Não garante fairness completa
- Pode haver starvation em alguns cenários

**Características:**
- Semáforo limitando a 4 filósofos
- Semáforos individuais para cada garfo
- Sistema de estatísticas
- Execucao por 2 minutos

### Tarefa 4: Solução com Monitores e Fairness

Esta solução usa uma classe Mesa como monitor centralizado que gerencia o acesso aos garfos com garantia de fairness.

**Como funciona:**

A classe Mesa atua como monitor, controlando o acesso aos garfos de forma centralizada. Ela implementa um mecanismo que verifica o tempo de espera de cada filósofo e prioriza aqueles que esperaram mais tempo, garantindo que todos tenham oportunidade de comer.

**Como garante fairness:**

O monitor rastreia quando cada filósofo comeu pela última vez. Se um filósofo esperar mais de 5 segundos, ele tem prioridade. Além disso, o sistema verifica se algum filósofo está sendo muito mais favorecido que outros, garantindo distribuição justa.

**Como previne deadlock e starvation:**

- Deadlock: O monitor controla centralizadamente o acesso, garantindo que apenas filósofos que podem pegar ambos os garfos simultaneamente sejam autorizados.
- Starvation: O mecanismo de priorização baseado em tempo de espera garante que todos os filósofos eventualmente comam.

**Características:**
- Monitor centralizado (classe Mesa)
- Mecanismo de fairness
- Prevenção de deadlock e starvation
- Sistema de estatísticas
- Execucao por 2 minutos

## Testes

Para testar cada solução, execute o programa correspondente e observe:
- Logs de execução mostrando as ações dos filósofos
- Estatísticas finais mostrando quantas vezes cada filósofo comeu
- Ausência de deadlock (nas tarefas 2, 3 e 4)
- Distribuição justa de oportunidades (especialmente na tarefa 4)

## Relatório Comparativo

Para uma análise detalhada das métricas e comparação entre as soluções, consulte o [RELATORIO.md](RELATORIO.md).

## Requisitos

- Java JDK 8 ou superior
- Sistema operacional com suporte a threads Java

## Observações

- Todos os programas executam em loop infinito até serem interrompidos
- Os tempos de execução estão configurados nas constantes de cada classe principal
- Os logs são impressos no console em tempo real
- As estatísticas são exibidas ao final da execução

