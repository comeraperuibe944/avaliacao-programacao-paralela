/**
 * Representa um garfo compartilhado no problema dos Filósofos Jantando - Tarefa 2.
 * 
 * DECISÃO DE DESIGN: Implementação idêntica à Tarefa 1. A solução de deadlock
 * não está na classe Garfo, mas na estratégia de aquisição dos filósofos.
 * 
 * MECANISMO: Usa sincronização Java nativa (synchronized, wait, notifyAll)
 * para garantir exclusão mútua e comunicação entre threads.
 */
public class Garfo {
    private final int id;
    private boolean emUso;

    public Garfo(int id) {
        this.id = id;
        this.emUso = false;
    }

    /**
     * Método para pegar o garfo.
     * 
     * DECISÃO: Loop while para proteger contra spurious wakeups.
     * Bloqueia até o garfo estar disponível.
     */
    public synchronized void pegar() throws InterruptedException {
        while (emUso) {
            wait();
        }
        emUso = true;
    }

    /**
     * Método para soltar o garfo.
     * 
     * DECISÃO: notifyAll() garante que todas as threads esperando
     * sejam notificadas, evitando starvation.
     */
    public synchronized void soltar() {
        emUso = false;
        notifyAll();
    }

    public int getId() {
        return id;
    }
}

