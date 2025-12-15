import java.util.Random;

/**
 * Representa um filósofo no problema dos Filósofos Jantando - Tarefa 4.
 * 
 * DECISÃO DE DESIGN: Simplificação através de delegação para a Mesa.
 * 
 * ARQUITETURA SIMPLIFICADA:
 * - Filósofo não conhece garfos individuais, apenas a Mesa
 * - Toda lógica de sincronização está encapsulada na Mesa
 * - Filósofo apenas solicita "pegar garfos" e "soltar garfos"
 * 
 * VANTAGENS:
 * - Código do filósofo mais simples e focado em comportamento
 * - Lógica de sincronização centralizada facilita manutenção
 * - Mudanças na estratégia de sincronização não afetam esta classe
 * 
 * COMPARAÇÃO COM TAREFAS ANTERIORES:
 * - Tarefa 1-3: Filósofo gerencia garfos diretamente
 * - Tarefa 4: Filósofo delega para Mesa (padrão de design mais limpo)
 */
public class Filosofo extends Thread {
    private final int id;
    // DECISÃO: Referência à Mesa em vez de garfos individuais
    // Abstrai a complexidade da gestão de recursos
    private final Mesa mesa;
    private final Random random;
    private int vezesComeu;

    /**
     * Construtor do filósofo.
     * 
     * DECISÃO: Recebe apenas a Mesa, não garfos individuais.
     * A Mesa gerencia quais garfos pertencem a cada filósofo.
     */
    public Filosofo(int id, Mesa mesa) {
        this.id = id;
        this.mesa = mesa;
        this.random = new Random();
        this.vezesComeu = 0;
    }

    /**
     * Método principal da thread do filósofo.
     * 
     * DECISÃO: Comportamento idêntico às outras tarefas - ciclo pensar/comer.
     * A diferença está na implementação do método comer().
     */
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
     * DECISÃO DE DESIGN: Delegação completa para a Mesa.
     * 
     * SIMPLICIDADE:
     * - Não precisa gerenciar ordem de aquisição de garfos
     * - Não precisa conhecer quais garfos são seus
     * - A Mesa abstrai toda a complexidade
     * 
     * FLUXO:
     * 1. Solicita garfos à Mesa (bloqueia até conseguir)
     * 2. Come (simulado com sleep)
     * 3. Informa à Mesa que terminou (libera garfos)
     * 
     * A Mesa implementa lógica de prevenção de starvation internamente.
     */
    private void comer() throws InterruptedException {
        // DECISÃO: Método único pegarGarfos() em vez de pegar garfos separadamente
        // A Mesa decide quando permitir que o filósofo pegue seus garfos
        log("tentando pegar os garfos");
        mesa.pegarGarfos(id); // Bloqueia até conseguir ambos os garfos
        log("pegou os garfos e comecou a comer");

        int tempo = 1000 + random.nextInt(2000);
        vezesComeu++;
        Thread.sleep(tempo);

        // DECISÃO: Método único soltarGarfos() libera ambos os garfos
        mesa.soltarGarfos(id);
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

