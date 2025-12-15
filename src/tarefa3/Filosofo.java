import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Representa um filósofo no problema dos Filósofos Jantando - Tarefa 3.
 * 
 * DECISÃO DE DESIGN: Usa semáforo limitador para prevenir deadlock.
 * 
 * ESTRATÉGIA DE PREVENÇÃO DE DEADLOCK:
 * Antes de tentar pegar os garfos, o filósofo deve adquirir uma permissão
 * do semáforo. Como há apenas 4 permissões para 5 filósofos, no máximo
 * 4 filósofos tentarão pegar garfos simultaneamente. Isso garante que
 * sempre haverá pelo menos 1 garfo disponível, prevenindo deadlock.
 * 
 * FLUXO:
 * 1. Adquire permissão do semáforo (pode bloquear se todas estiverem em uso)
 * 2. Pega garfo esquerdo
 * 3. Pega garfo direito
 * 4. Come
 * 5. Solta ambos os garfos
 * 6. Libera permissão do semáforo (em finally para garantir liberação)
 */
public class Filosofo extends Thread {
    private final int id;
    private final Garfo garfoEsquerdo;
    private final Garfo garfoDireito;
    // DECISÃO: Referência ao semáforo compartilhado - todos competem pelas mesmas permissões
    private final Semaphore semaforoLimite;
    private final Random random;
    private int vezesComeu;

    /**
     * Construtor do filósofo.
     * 
     * DECISÃO: Recebe o semáforo compartilhado como parâmetro para garantir
     * que todos os filósofos compartilhem o mesmo limitador de concorrência.
     */
    public Filosofo(int id, Garfo garfoEsquerdo, Garfo garfoDireito, Semaphore semaforoLimite) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
        this.semaforoLimite = semaforoLimite;
        this.random = new Random();
        this.vezesComeu = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();
                comer();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void pensar() throws InterruptedException {
        int tempo = 1000 + random.nextInt(2000);
        log("comecou a pensar");
        Thread.sleep(tempo);
    }

    /**
     * Simula o filósofo comendo.
     * 
     * DECISÃO DE DESIGN CRÍTICA: Uso de semáforo antes de pegar garfos.
     * 
     * PROTEÇÃO CONTRA DEADLOCK:
     * O semáforo garante que no máximo 4 filósofos tentem pegar garfos
     * simultaneamente. Com 5 garfos e no máximo 4 filósofos competindo,
     * sempre haverá pelo menos 1 garfo disponível, prevenindo deadlock.
     * 
     * DECISÃO: try-finally garante que o semáforo seja sempre liberado,
     * mesmo se ocorrer exceção durante a alimentação.
     * 
     * ORDEM DE AQUISIÇÃO:
     * 1. Semáforo (limita concorrência)
     * 2. Garfo esquerdo
     * 3. Garfo direito
     * 4. Comer
     * 5. Soltar garfos
     * 6. Liberar semáforo (finally)
     */
    private void comer() throws InterruptedException {
        // DECISÃO: Adquirir semáforo ANTES de tentar pegar garfos
        // Isso limita quantos filósofos competem simultaneamente
        semaforoLimite.acquire();
        try {
            // DECISÃO: Ordem padrão (esquerdo -> direito) funciona agora
            // porque o semáforo previne que todos tentem simultaneamente
            log("tentando pegar garfo esquerdo " + garfoEsquerdo.getId());
            garfoEsquerdo.pegar();
            log("pegou garfo esquerdo " + garfoEsquerdo.getId());

            log("tentando pegar garfo direito " + garfoDireito.getId());
            garfoDireito.pegar();
            log("pegou garfo direito " + garfoDireito.getId());

            int tempo = 1000 + random.nextInt(2000);
            log("comecou a comer");
            vezesComeu++;
            Thread.sleep(tempo);

            garfoEsquerdo.soltar();
            garfoDireito.soltar();
            log("terminou de comer e soltou os garfos");
        } finally {
            // DECISÃO CRÍTICA: Sempre liberar semáforo, mesmo em caso de exceção
            // Garante que outras threads não fiquem bloqueadas permanentemente
            semaforoLimite.release();
        }
    }

    private void log(String mensagem) {
        System.out.println("Filosofo " + id + ": " + mensagem);
    }

    public int getFilosofoId() {
        return id;
    }

    public int getVezesComeu() {
        return vezesComeu;
    }
}

