import java.util.Random;

/**
 * Representa um filósofo no problema dos Filósofos Jantando - Tarefa 2.
 * 
 * DECISÃO DE DESIGN PRINCIPAL: Quebra de simetria para prevenir deadlock.
 * O filósofo com ID 4 inverte a ordem de pegar os garfos, pegando primeiro
 * o direito e depois o esquerdo. Todos os outros seguem a ordem padrão
 * (esquerdo -> direito).
 * 
 * COMO RESOLVE O DEADLOCK:
 * Se todos os filósofos 0-3 pegarem seus garfos esquerdos simultaneamente,
 * o filósofo 4 tentará pegar seu garfo direito primeiro. Como o garfo direito
 * do filósofo 4 é o garfo esquerdo do filósofo 0, e o filósofo 0 já o tem,
 * o filósofo 4 ficará bloqueado. Isso permite que pelo menos um filósofo
 * (o 0) complete a aquisição e libere seus garfos, quebrando o ciclo.
 */
public class Filosofo extends Thread {
    // DECISÃO: ID imutável usado para determinar estratégia de aquisição
    private final int id;
    private final Garfo garfoEsquerdo;
    private final Garfo garfoDireito;
    private final Random random;
    private int vezesComeu;

    public Filosofo(int id, Garfo garfoEsquerdo, Garfo garfoDireito) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
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
     * DECISÃO DE DESIGN CRÍTICA: Estratégia condicional baseada no ID.
     * - Filósofo 4: ordem invertida (direito -> esquerdo)
     * - Outros filósofos: ordem padrão (esquerdo -> direito)
     * 
     * ANÁLISE DA SOLUÇÃO:
     * Se todos os filósofos 0-3 pegarem seus garfos esquerdos:
     * - Filósofo 0 tem garfo 0 (esquerdo), precisa de garfo 1 (direito)
     * - Filósofo 1 tem garfo 1 (esquerdo), precisa de garfo 2 (direito)
     * - Filósofo 2 tem garfo 2 (esquerdo), precisa de garfo 3 (direito)
     * - Filósofo 3 tem garfo 3 (esquerdo), precisa de garfo 4 (direito)
     * - Filósofo 4 tenta pegar garfo 4 (direito) primeiro, mas filósofo 0 já tem garfo 0
     * 
     * Como o filósofo 4 não consegue pegar o garfo direito (que é o esquerdo do 0),
     * ele não bloqueia o garfo esquerdo do 0. Assim, o filósofo 0 pode completar
     * e liberar seus garfos, quebrando o deadlock.
     */
    private void comer() throws InterruptedException {
        // DECISÃO: Filósofo 4 usa ordem invertida para quebrar simetria
        if (id == 4) {
            // ESTRATÉGIA INVERTIDA: Direito primeiro, depois esquerdo
            log("tentando pegar garfo direito " + garfoDireito.getId());
            garfoDireito.pegar();
            log("pegou garfo direito " + garfoDireito.getId());

            log("tentando pegar garfo esquerdo " + garfoEsquerdo.getId());
            garfoEsquerdo.pegar();
            log("pegou garfo esquerdo " + garfoEsquerdo.getId());
        } else {
            // ESTRATÉGIA PADRÃO: Esquerdo primeiro, depois direito
            log("tentando pegar garfo esquerdo " + garfoEsquerdo.getId());
            garfoEsquerdo.pegar();
            log("pegou garfo esquerdo " + garfoEsquerdo.getId());

            log("tentando pegar garfo direito " + garfoDireito.getId());
            garfoDireito.pegar();
            log("pegou garfo direito " + garfoDireito.getId());
        }

        // DECISÃO: Comportamento após pegar garfos é idêntico para todos
        int tempo = 1000 + random.nextInt(2000);
        log("comecou a comer");
        vezesComeu++;
        Thread.sleep(tempo);

        // DECISÃO: Ordem de soltar não importa, mas mantemos consistência
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

