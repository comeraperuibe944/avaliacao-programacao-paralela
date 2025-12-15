/**
 * Classe principal que implementa o problema dos Filósofos Jantando - Tarefa 4.
 * 
 * DECISÃO DE DESIGN: Solução usando classe Mesa centralizada com prevenção de starvation.
 * 
 * ARQUITETURA:
 * - Mesa: gerencia todos os garfos e implementa lógica de prevenção de starvation
 * - Filosofo: delega toda a lógica de aquisição de garfos para a Mesa
 * 
 * VANTAGENS:
 * - Centralização da lógica de sincronização facilita manutenção
 * - Prevenção explícita de starvation através de timestamps
 * - Garfos não são objetos separados, apenas índices na Mesa
 * - Mais fácil de estender com políticas adicionais
 * 
 * DIFERENÇA DAS OUTRAS TAREFAS:
 * - Tarefa 1-3: Filósofos gerenciam garfos diretamente
 * - Tarefa 4: Mesa centraliza toda a lógica de sincronização
 */
public class Main {
    private static final int NUM_FILOSOFOS = 5;
    private static final int TEMPO_EXECUCAO_MS = 120000;

    public static void main(String[] args) {
        // DECISÃO: Criar uma única instância de Mesa que gerencia todos os recursos
        Mesa mesa = new Mesa(NUM_FILOSOFOS);
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];

        // DECISÃO: Filósofos recebem apenas referência à Mesa, não aos garfos individuais
        // A Mesa abstrai a complexidade da gestão de recursos
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            filosofos[i].start();
        }

        try {
            Thread.sleep(TEMPO_EXECUCAO_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Finalizando execucao ===");
        for (Filosofo filosofo : filosofos) {
            filosofo.interrupt();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Estatisticas ===");
        for (Filosofo filosofo : filosofos) {
            System.out.println("Filosofo " + filosofo.getFilosofoId() + " comeu " + filosofo.getVezesComeu() + " vezes");
        }
    }
}

