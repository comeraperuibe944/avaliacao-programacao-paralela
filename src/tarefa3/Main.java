import java.util.concurrent.Semaphore;

/**
 * Classe principal que implementa o problema dos Filósofos Jantando - Tarefa 3.
 * 
 * DECISÃO DE DESIGN: Solução usando semáforo limitador para prevenir deadlock.
 * 
 * ESTRATÉGIA:
 * Limita a quantidade de filósofos que podem tentar comer simultaneamente
 * a 4 (um a menos que o total de 5). Isso garante que sempre haverá pelo
 * menos um garfo disponível, pois com 4 filósofos tentando comer, no máximo
 * 4 garfos estarão em uso, deixando pelo menos 1 livre.
 * 
 * VANTAGENS:
 * - Previne deadlock garantindo que nem todos os filósofos tentem comer ao mesmo tempo
 * - Mais justo que a solução da Tarefa 2 (não favorece um filósofo específico)
 * - Usa primitivas de sincronização de alto nível (Semaphore)
 * 
 * MECANISMO:
 * Cada filósofo deve adquirir o semáforo antes de tentar pegar os garfos.
 * Com apenas 4 permissões disponíveis, no máximo 4 filósofos competem pelos garfos.
 */
public class Main {
    private static final int NUM_FILOSOFOS = 5;
    private static final int TEMPO_EXECUCAO_MS = 120000;
    // DECISÃO CRÍTICA: Semáforo com 4 permissões (NUM_FILOSOFOS - 1)
    // Garante que sempre haverá pelo menos 1 garfo disponível
    private static final Semaphore semaforoLimite = new Semaphore(4);

    public static void main(String[] args) {
        Garfo[] garfos = new Garfo[NUM_FILOSOFOS];
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];

        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Garfo(i);
        }

        // DECISÃO: Passar semáforo compartilhado para todos os filósofos
        // Todos competem pelas mesmas 4 permissões
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % NUM_FILOSOFOS];
            // DECISÃO: Semáforo compartilhado injetado via construtor
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, semaforoLimite);
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

