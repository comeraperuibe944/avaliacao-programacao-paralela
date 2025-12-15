/**
 * Classe que gerencia a mesa e todos os garfos no problema dos Filósofos Jantando - Tarefa 4.
 * 
 * DECISÃO DE DESIGN PRINCIPAL: Centralização da lógica de sincronização.
 * 
 * RESPONSABILIDADES:
 * - Gerencia estado de todos os garfos (disponível/em uso)
 * - Controla quando filósofos podem pegar garfos
 * - Implementa prevenção de starvation usando timestamps
 * 
 * PREVENÇÃO DE STARVATION:
 * Rastreia quando cada filósofo comeu pela última vez. Se um filósofo
 * esperou muito tempo (>5s) e outro filósofo comeu recentemente
 * (diferença >2s), prioriza o que esperou mais tempo.
 * 
 * VANTAGENS:
 * - Garante justiça (fairness) entre filósofos
 * - Previne que um filósofo fique sem comer indefinidamente
 * - Lógica centralizada facilita manutenção e testes
 */
public class Mesa {
    // DECISÃO: Array de booleanos para rastrear disponibilidade de cada garfo
    // Índice i representa o garfo i
    private final boolean[] garfosDisponiveis;
    // DECISÃO: Array de timestamps para rastrear última vez que cada filósofo comeu
    // Usado para prevenir starvation - filósofos que esperaram muito têm prioridade
    private final long[] ultimaVezComeu;
    private final int numFilosofos;

    /**
     * Construtor da Mesa.
     * 
     * DECISÃO: Inicializar todos os garfos como disponíveis e timestamps como 0.
     * Timestamp 0 significa que o filósofo nunca comeu, dando-lhe prioridade inicial.
     */
    public Mesa(int numFilosofos) {
        this.numFilosofos = numFilosofos;
        this.garfosDisponiveis = new boolean[numFilosofos];
        this.ultimaVezComeu = new long[numFilosofos];
        // DECISÃO: Todos os garfos começam disponíveis
        for (int i = 0; i < numFilosofos; i++) {
            garfosDisponiveis[i] = true;
            ultimaVezComeu[i] = 0; // 0 = nunca comeu, terá prioridade
        }
    }

    /**
     * Método para um filósofo pegar seus dois garfos.
     * 
     * DECISÃO DE DESIGN: Método sincronizado que bloqueia até conseguir ambos os garfos.
     * 
     * COMPORTAMENTO:
     * - Calcula quais garfos o filósofo precisa (esquerdo e direito)
     * - Verifica se pode comer usando lógica de prevenção de starvation
     * - Bloqueia até que possa comer
     * - Marca garfos como indisponíveis e atualiza timestamp
     * 
     * DECISÃO: Garantir aquisição atômica de ambos os garfos evita deadlock
     * parcial (ter um garfo mas não o outro).
     */
    public synchronized void pegarGarfos(int filosofoId) throws InterruptedException {
        // DECISÃO: Calcular índices dos garfos baseado no ID do filósofo
        // Estrutura circular: filósofo i precisa garfos i e (i+1) mod n
        int garfoEsquerdo = filosofoId;
        int garfoDireito = (filosofoId + 1) % numFilosofos;

        // DECISÃO: Loop while para verificar condições antes de pegar garfos
        // podeComer() implementa lógica de prevenção de starvation
        while (!podeComer(filosofoId, garfoEsquerdo, garfoDireito)) {
            wait(); // Bloqueia até ser notificado que condições mudaram
        }

        // DECISÃO: Marcar garfos como indisponíveis atomicamente
        // Garante que ninguém mais pode pegá-los até serem liberados
        garfosDisponiveis[garfoEsquerdo] = false;
        garfosDisponiveis[garfoDireito] = false;
        // DECISÃO: Atualizar timestamp quando filósofo começa a comer
        // Usado para calcular tempo de espera na próxima tentativa
        ultimaVezComeu[filosofoId] = System.currentTimeMillis();
    }

    /**
     * Método para um filósofo soltar seus dois garfos.
     * 
     * DECISÃO: Método sincronizado que libera ambos os garfos atomicamente
     * e notifica todas as threads esperando.
     * 
     * COMPORTAMENTO:
     * - Calcula quais garfos o filósofo está usando
     * - Marca garfos como disponíveis
     * - Notifica todas as threads esperando (pode acordar múltiplos filósofos)
     */
    public synchronized void soltarGarfos(int filosofoId) {
        int garfoEsquerdo = filosofoId;
        int garfoDireito = (filosofoId + 1) % numFilosofos;

        // DECISÃO: Liberar ambos os garfos atomicamente
        garfosDisponiveis[garfoEsquerdo] = true;
        garfosDisponiveis[garfoDireito] = true;
        // DECISÃO: notifyAll() acorda todos os filósofos esperando
        // Cada um verificará novamente se pode comer em podeComer()
        notifyAll();
    }

    /**
     * Verifica se um filósofo pode comer agora.
     * 
     * DECISÃO DE DESIGN: Implementa lógica de prevenção de starvation.
     * 
     * CONDIÇÕES PARA COMER:
     * 1. Ambos os garfos devem estar disponíveis (condição básica)
     * 2. Se o filósofo esperou muito (>5s), pode comer imediatamente
     * 3. Caso contrário, verifica se algum outro filósofo esperou muito mais
     *    (diferença >2s) - se sim, dá prioridade ao que esperou mais
     * 
     * LÓGICA DE FAIRNESS:
     * - Evita que um filósofo fique sem comer indefinidamente
     * - Prioriza filósofos que esperaram mais tempo
     * - Permite que outros comam se não há risco de starvation
     * 
     * THRESHOLDS:
     * - 5000ms: tempo máximo de espera antes de ter prioridade absoluta
     * - 2000ms: diferença mínima para dar prioridade a outro filósofo
     */
    private boolean podeComer(int filosofoId, int garfoEsquerdo, int garfoDireito) {
        // DECISÃO: Verificação básica - ambos os garfos devem estar disponíveis
        if (!garfosDisponiveis[garfoEsquerdo] || !garfosDisponiveis[garfoDireito]) {
            return false;
        }

        // DECISÃO: Calcular quanto tempo o filósofo esperou desde a última vez que comeu
        long tempoEspera = System.currentTimeMillis() - ultimaVezComeu[filosofoId];
        
        // DECISÃO: Se esperou mais de 5 segundos, tem prioridade absoluta
        // Previne starvation garantindo que filósofos não esperem indefinidamente
        if (tempoEspera > 5000) {
            return true;
        }

        // DECISÃO: Verificar se algum outro filósofo esperou muito mais tempo
        // Se sim, dar prioridade a ele para garantir justiça
        for (int i = 0; i < numFilosofos; i++) {
            if (i != filosofoId) {
                long tempoEsperaOutro = System.currentTimeMillis() - ultimaVezComeu[i];
                // DECISÃO: Se outro filósofo esperou mais de 2s a mais, dar prioridade a ele
                // Isso garante que filósofos que esperaram muito não sejam preteridos
                if (tempoEsperaOutro > tempoEspera + 2000) {
                    return false; // Outro filósofo tem prioridade
                }
            }
        }

        // DECISÃO: Se não há risco de starvation e garfos estão disponíveis, pode comer
        return true;
    }
}

