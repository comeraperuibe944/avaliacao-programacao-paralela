/**
 * Classe principal que implementa o problema dos Filósofos Jantando - Tarefa 2.
 * 
 * DECISÃO DE DESIGN: Esta implementação resolve o deadlock da Tarefa 1
 * usando a técnica de "quebrar a simetria". Um filósofo (o de ID 4) inverte
 * a ordem de pegar os garfos, pegando primeiro o direito e depois o esquerdo.
 * 
 * SOLUÇÃO DE DEADLOCK:
 * Ao quebrar a simetria, garantimos que nem todos os filósofos seguirão
 * a mesma ordem de aquisição. Isso previne o cenário onde todos pegam
 * o garfo esquerdo simultaneamente e ficam bloqueados.
 * 
 * VANTAGENS:
 * - Simples de implementar
 * - Não requer recursos adicionais (semáforos extras)
 * - Resolve deadlock eficientemente
 */
public class Main {
    // DECISÃO: 5 filósofos mantém o problema clássico
    private static final int NUM_FILOSOFOS = 5;
    // DECISÃO: 120 segundos permite observar comportamento por mais tempo
    private static final int TEMPO_EXECUCAO_MS = 120000;

    public static void main(String[] args) {
        Garfo[] garfos = new Garfo[NUM_FILOSOFOS];
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];

        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Garfo(i);
        }

        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % NUM_FILOSOFOS];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito);
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

