/**
 * Classe principal que implementa o problema clássico dos Filósofos Jantando.
 * 
 * DECISÃO DE DESIGN: Esta é a implementação básica (Tarefa 1) que demonstra
 * o problema de deadlock. Todos os filósofos seguem a mesma estratégia:
 * pegam primeiro o garfo esquerdo e depois o direito. Esta uniformidade
 * pode levar a um deadlock quando todos os filósofos pegam seu garfo esquerdo
 * simultaneamente, ficando bloqueados esperando o direito.
 * 
 * ESTRUTURA DO PROGRAMA:
 * - Cria 5 filósofos e 5 garfos em uma mesa circular
 * - Cada filósofo precisa de 2 garfos adjacentes para comer
 * - Executa por um tempo determinado e depois finaliza mostrando estatísticas
 */
public class Main {
    // DECISÃO: 5 filósofos é o número clássico do problema, suficiente para demonstrar deadlock
    private static final int NUM_FILOSOFOS = 5;
    // DECISÃO: 30 segundos permite observar o comportamento sem execução muito longa
    private static final int TEMPO_EXECUCAO_MS = 30000;

    public static void main(String[] args) {
        // DECISÃO: Array de garfos compartilhados - cada garfo é compartilhado por 2 filósofos
        Garfo[] garfos = new Garfo[NUM_FILOSOFOS];
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];

        // DECISÃO: Criar garfos primeiro, pois são recursos compartilhados necessários para os filósofos
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Garfo(i);
        }

        // DECISÃO: Configuração circular - filósofo i compartilha garfo i (esquerdo) e garfo (i+1) mod 5 (direito)
        // Isso cria a dependência circular que caracteriza o problema
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo garfoEsquerdo = garfos[i];
            // DECISÃO: Uso de módulo para criar estrutura circular - último filósofo compartilha garfo 0
            Garfo garfoDireito = garfos[(i + 1) % NUM_FILOSOFOS];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito);
            // DECISÃO: Iniciar thread imediatamente após criação para simular comportamento concorrente
            filosofos[i].start();
        }

        // DECISÃO: Aguardar tempo de execução para permitir que os filósofos executem seus ciclos
        try {
            Thread.sleep(TEMPO_EXECUCAO_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // DECISÃO: Usar interrupt() para sinalizar término - permite finalização graciosa
        System.out.println("\n=== Finalizando execucao ===");
        for (Filosofo filosofo : filosofos) {
            filosofo.interrupt();
        }

        // DECISÃO: Aguardar 1 segundo para dar tempo das threads finalizarem antes de coletar estatísticas
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // DECISÃO: Mostrar estatísticas ao final para avaliar justiça e eficiência da solução
        System.out.println("\n=== Estatisticas ===");
        for (Filosofo filosofo : filosofos) {
            System.out.println("Filosofo " + filosofo.getFilosofoId() + " comeu " + filosofo.getVezesComeu() + " vezes");
        }
    }
}

