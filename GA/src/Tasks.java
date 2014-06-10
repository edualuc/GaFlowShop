import java.util.Scanner;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.PermutationChromosome;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.SwapMutator;


public class Tasks {

	private static int[][] matrizMaqsTarefas;
	private static int[] tempoTotalOps;
	private static int maquinas;
	private static int tarefas;

	public static void main(String[] args) {
		
		/*
		 * Obter os dados
		 */
		getDados();
		
		/*
		 *  Definir ambiente
		 */
		Function<Genotype<EnumGene<Integer>>, Integer> fitness = 
				new Fitness(matrizMaqsTarefas);
		Factory<Genotype<EnumGene<Integer>>> genotipo = 
				Genotype.of( PermutationChromosome.ofInteger(tarefas) );
		final GeneticAlgorithm<EnumGene<Integer>, Integer> ga = 
				new GeneticAlgorithm<>(genotipo, fitness, Optimize.MINIMUM);
		
		/*
		 *  Definir Operadores
		 */
		ga.setSelectors(new RouletteWheelSelector<EnumGene<Integer>>());
		ga.setAlterers(new PartiallyMatchedCrossover<Integer>(0.1),
				new SwapMutator<EnumGene<Integer>>(0.01)); 
		
		/*
		 *  Rodar Ambiente
		 */
		ga.setup();
        ga.evolve(700);
		
		/*
		 *  Informar resultados
		 */
        System.out.println(ga.getBestStatistics());
        System.out.println(ga.getBestPhenotype());
		
	}
	
	private static void getDados() {
		Scanner scan = new Scanner(System.in);
		
		maquinas = scan.nextInt();
		tarefas = scan.nextInt();
		
		tempoTotalOps = new int[tarefas];
		
		matrizMaqsTarefas = new int[tarefas][maquinas];
		
		// leitura e descarte dos 1s do arquivo de entrada
		for (int i = 0; i < maquinas - 1; i++) {
			scan.nextInt();
		}

		// leitura da matriz de entrada
		for (int i = 0; i < tarefas; i++) {
			tempoTotalOps[i] = 0;
			for (int j = 0; j < maquinas; j++) {
				matrizMaqsTarefas[i][j] = scan.nextInt();
				tempoTotalOps[i] += matrizMaqsTarefas[i][j]; // soma total
			}
		}
		
		scan.close();
	}

}

class Fitness implements Function<Genotype<EnumGene<Integer>>, Integer>
{
	private int matrizMaqsOps[][];
	public Fitness(int matrizMaqsTarefas[][]) {
		this.matrizMaqsOps = matrizMaqsTarefas;
	}
	
    @Override
    public Integer apply(Genotype<EnumGene<Integer>> genotype) {
        
    	Chromosome<EnumGene<Integer>> cromossomo = genotype.getChromosome();
    	int alelosDoGene[] = new int[100];
    	
    	for (int i = 0, n = cromossomo.length(); i < n; ++i) {
            alelosDoGene[i] = cromossomo.getGene(i).getAllele();
            System.out.print(cromossomo.getGene(i).getAllele() + " ");
        }
    	System.out.println();
    	
        return calculaMakespan(alelosDoGene);
    }
    
    int calculaMakespan(int[] solucao) {
		int[][] shifts = new int[matrizMaqsOps.length][matrizMaqsOps[0].length];

		for (int i = 0; i < shifts.length; i++) {
			for (int j = 0; j < shifts[0].length; j++) {
				if (i == 0) { // se é a primeira operação
					if (j == 0) { // se é a primeira tarefa
						shifts[0][0] = matrizMaqsOps[solucao[i]][0];

					} else {// se são as demais tarefas
						shifts[i][j] = shifts[i][j - 1]
								+ matrizMaqsOps[solucao[i]][j];

					}
				} else { // se são as demais operações (>0)
					if (j == 0) { // se é a primeira tarefa
						shifts[i][0] = shifts[i - 1][0]
								+ matrizMaqsOps[solucao[i]][0];

					} else { // se são as demais tarefas
						shifts[i][j] = Math.max(shifts[i - 1][j],
								shifts[i][j - 1])
								+ matrizMaqsOps[solucao[i]][j];
					}
				}
			}
			// imprimeVetor(shifts[i]);
		}

		return shifts[matrizMaqsOps.length - 1][matrizMaqsOps[0].length - 1];
	}
}
 
