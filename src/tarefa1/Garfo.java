/**
 * Representa um garfo compartilhado no problema dos Filósofos Jantando.
 * 
 * DECISÃO DE DESIGN: Cada garfo é um recurso compartilhado que pode ser
 * usado por apenas um filósofo por vez. Usa sincronização Java nativa
 * (synchronized, wait, notifyAll) para garantir exclusão mútua.
 * 
 * MECANISMO DE SINCRONIZAÇÃO:
 * - synchronized: garante que apenas uma thread acessa o estado por vez
 * - wait(): bloqueia thread quando garfo está em uso
 * - notifyAll(): acorda todas as threads esperando quando garfo é liberado
 * 
 * DECISÃO: notifyAll() em vez de notify() para evitar starvation,
 * garantindo que todas as threads esperando tenham chance de executar.
 */
public class Garfo {
    // DECISÃO: ID imutável para identificação e debug
    private final int id;
    // DECISÃO: Flag booleana simples para indicar estado - protegida por synchronized
    private boolean emUso;

    /**
     * Construtor do garfo.
     * 
     * DECISÃO: Inicializar como disponível (emUso = false) para que
     * o primeiro filósofo possa pegá-lo imediatamente.
     */
    public Garfo(int id) {
        this.id = id;
        this.emUso = false;
    }

    /**
     * Método para pegar o garfo.
     * 
     * DECISÃO DE DESIGN: Uso de synchronized no método para garantir
     * exclusão mútua no acesso ao estado do garfo.
     * 
     * DECISÃO: Loop while em vez de if para evitar notificações espúrias
     * (spurious wakeups) - padrão recomendado em Java.
     * 
     * COMPORTAMENTO:
     * - Se garfo está livre: marca como em uso e retorna
     * - Se garfo está em uso: bloqueia thread até ser liberado
     */
    public synchronized void pegar() throws InterruptedException {
        // DECISÃO: while em vez de if para proteger contra spurious wakeups
        while (emUso) {
            wait(); // Libera o lock e espera até ser notificado
        }
        // DECISÃO: Marcar como em uso apenas após sair do loop
        emUso = true;
    }

    /**
     * Método para soltar o garfo.
     * 
     * DECISÃO DE DESIGN: synchronized para garantir visibilidade das mudanças
     * de estado para outras threads.
     * 
     * DECISÃO: notifyAll() em vez de notify() para evitar que uma thread
     * fique permanentemente bloqueada se notify() acordar a thread errada.
     */
    public synchronized void soltar() {
        emUso = false;
        // DECISÃO: notifyAll() garante que todas as threads esperando sejam acordadas
        // e tenham chance de competir pelo garfo, evitando starvation
        notifyAll();
    }

    /**
     * Retorna o ID do garfo.
     * 
     * DECISÃO: Método simples de acesso - não precisa sincronização pois
     * o ID é imutável e final.
     */
    public int getId() {
        return id;
    }
}

