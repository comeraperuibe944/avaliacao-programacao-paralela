import java.util.Random;

/**
 * Representa um filósofo no problema dos Filósofos Jantando.
 * 
 * DECISÃO DE DESIGN: Esta classe estende Thread para que cada filósofo
 * execute em sua própria thread, simulando concorrência real.
 * 
 * COMPORTAMENTO:
 * - Ciclo infinito: pensar -> comer -> pensar -> comer...
 * - Para comer, precisa pegar dois garfos adjacentes
 * - Usa tempos aleatórios para simular comportamento realista
 * 
 * PROBLEMA DE DEADLOCK (Tarefa 1):
 * Todos os filósofos seguem a mesma ordem: esquerdo primeiro, depois direito.
 * Se todos pegarem o garfo esquerdo simultaneamente, nenhum conseguirá pegar
 * o direito, causando deadlock.
 */
public class Filosofo extends Thread {
    // DECISÃO: ID imutável para identificação única e thread-safe
    private final int id;
    // DECISÃO: Referências diretas aos garfos - cada filósofo conhece seus recursos
    private final Garfo garfoEsquerdo;
    private final Garfo garfoDireito;
    // DECISÃO: Random por instância para evitar contenção entre threads
    private final Random random;
    // DECISÃO: Contador não sincronizado - assumimos que leitura ocorre após join/interrupt
    private int vezesComeu;

    /**
     * Construtor do filósofo.
     * 
     * DECISÃO: Recebe referências aos garfos no construtor para estabelecer
     * a topologia circular da mesa antes da execução.
     */
    public Filosofo(int id, Garfo garfoEsquerdo, Garfo garfoDireito) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
        // DECISÃO: Random sem seed para variabilidade entre execuções
        this.random = new Random();
        this.vezesComeu = 0;
    }

    /**
     * Método principal da thread do filósofo.
     * 
     * DECISÃO DE DESIGN: Loop infinito com tratamento de InterruptedException.
     * O interrupt permite finalização graciosa quando o programa principal
     * sinaliza término.
     */
    @Override
    public void run() {
        try {
            // DECISÃO: Loop infinito simula comportamento contínuo dos filósofos
            while (true) {
                pensar();
                comer();
            }
        } catch (InterruptedException e) {
            // DECISÃO: Restaurar flag de interrupção para permitir propagação correta
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Simula o filósofo pensando.
     * 
     * DECISÃO: Tempo aleatório entre 1-3 segundos para simular comportamento
     * não determinístico e evitar sincronização artificial entre threads.
     */
    private void pensar() throws InterruptedException {
        int tempo = 1000 + random.nextInt(2000);
        log("comecou a pensar");
        Thread.sleep(tempo);
    }

    /**
     * Simula o filósofo comendo.
     * 
     * DECISÃO DE DESIGN CRÍTICA (Tarefa 1): Todos pegam esquerdo primeiro, depois direito.
     * Esta uniformidade causa deadlock quando todos pegam o esquerdo simultaneamente.
     * 
     * ORDEM DE AQUISIÇÃO:
     * 1. Pega garfo esquerdo (bloqueia até disponível)
     * 2. Pega garfo direito (bloqueia até disponível)
     * 3. Come (simulado com sleep)
     * 4. Solta ambos os garfos
     * 
     * PROBLEMA: Se todos os filósofos estiverem na etapa 1 simultaneamente,
     * nenhum conseguirá avançar para a etapa 2, causando deadlock.
     */
    private void comer() throws InterruptedException {
        // DECISÃO: Ordem fixa (esquerdo -> direito) para todos - causa deadlock potencial
        log("tentando pegar garfo esquerdo " + garfoEsquerdo.getId());
        garfoEsquerdo.pegar(); // Bloqueia até conseguir
        log("pegou garfo esquerdo " + garfoEsquerdo.getId());

        log("tentando pegar garfo direito " + garfoDireito.getId());
        garfoDireito.pegar(); // Pode ficar bloqueado aqui se outro filósofo já tiver o garfo
        log("pegou garfo direito " + garfoDireito.getId());

        // DECISÃO: Tempo aleatório de alimentação simula duração variável
        int tempo = 1000 + random.nextInt(2000);
        log("comecou a comer");
        vezesComeu++;
        Thread.sleep(tempo);

        // DECISÃO: Sempre soltar ambos os garfos, independente de exceções
        // (neste código simples, não há try-finally, mas seria ideal)
        garfoEsquerdo.soltar();
        garfoDireito.soltar();
        log("terminou de comer e soltou os garfos");
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

