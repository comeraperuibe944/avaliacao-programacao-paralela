import java.util.concurrent.Semaphore;

/**
 * Representa um garfo compartilhado no problema dos Filósofos Jantando - Tarefa 3.
 * 
 * DECISÃO DE DESIGN: Implementação usando Semaphore em vez de synchronized/wait/notify.
 * 
 * VANTAGENS DO SEMAPHORE:
 * - Código mais simples e legível
 * - Semaphore(1) funciona como um mutex binário (apenas 1 permissão)
 * - Não precisa de loops while para spurious wakeups
 * - API mais moderna e expressiva
 * 
 * COMPARAÇÃO COM TAREFA 1/2:
 * - Tarefa 1/2: synchronized + wait/notifyAll (mais verboso)
 * - Tarefa 3: Semaphore (mais conciso)
 * 
 * FUNCIONAMENTO:
 * - acquire(): bloqueia até conseguir a permissão (garfo disponível)
 * - release(): libera a permissão (garfo disponível novamente)
 */
public class Garfo {
    private final int id;
    // DECISÃO: Semaphore com 1 permissão = mutex binário
    // Garante que apenas 1 filósofo pode ter o garfo por vez
    private final Semaphore semaforo;

    /**
     * Construtor do garfo.
     * 
     * DECISÃO: Semaphore(1) cria um semáforo binário que permite
     * apenas uma thread por vez, equivalente a um lock exclusivo.
     */
    public Garfo(int id) {
        this.id = id;
        this.semaforo = new Semaphore(1);
    }

    /**
     * Método para pegar o garfo.
     * 
     * DECISÃO: acquire() bloqueia até conseguir a permissão.
     * Mais simples que synchronized + while + wait().
     */
    public void pegar() throws InterruptedException {
        semaforo.acquire();
    }

    /**
     * Método para soltar o garfo.
     * 
     * DECISÃO: release() libera a permissão, acordando uma thread esperando.
     * Semaphore gerencia a fila de espera automaticamente.
     */
    public void soltar() {
        semaforo.release();
    }

    public int getId() {
        return id;
    }
}

