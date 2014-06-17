import java.util.Scanner;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SwapMutator;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;


public class Tasks {
	private static final int sizePupulation = 50;
	private static final double probalidadeCrossover = 0.2;
	private static final double probalidadeMutacao = 0.01;
	private static final int quantidadeGeracoes = 990;
	
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
		 * Escolha dos melhores
		 */
		ga.setSelectors(new RouletteWheelSelector<EnumGene<Integer>, Integer>() );
		
		ga.setAlterers( 
				new PartiallyMatchedCrossover<Integer>(probalidadeCrossover),
				new SwapMutator<EnumGene<Integer>>(probalidadeMutacao)
		); 
		
		/*
		 *  Rodar Ambiente
		 */
		ga.setup();
		ga.setPopulationSize(sizePupulation);
		//ga.setMaximalPhenotypeAge(age);
		//ga.setSurvivorSelector(selector);
		
		/*
		for(int i = 0; i < quantidadeGeracoes; i = i + quantidadeGeracoes/10) {
			ga.evolve(quantidadeGeracoes/10);
			//System.out.println(ga.getStatistics());
			System.out.println("#" + ga.getStatistics().getGeneration() + 
					". Melhor: " + ga.getStatistics().getBestFitness() + 
					". Pior: " + ga.getStatistics().getWorstFitness());
		}
		*/
		ga.evolve(quantidadeGeracoes);
		
		/*
		 *  Informar resultados
		 */
		//System.out.println(ga.getTimeStatistics());
        //System.out.println(ga.getBestStatistics());
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
    	int alelosDoGene[] = new int[cromossomo.length()];
    	
    	for (int i = 0, n = cromossomo.length(); i < n; ++i) {
            alelosDoGene[i] = cromossomo.getGene(i).getAllele();
            //System.out.print(cromossomo.getGene(i).getAllele() + " ");
        }
    	//System.out.println();
    	
        return calculaMakespan(alelosDoGene);
    }
    
    int calculaMakespan(int[] solucao) {
		int[][] shifts = new int[matrizMaqsOps.length][matrizMaqsOps[0].length];

		for (int i = 0; i < shifts.length; i++) {
			for (int j = 0; j < shifts[0].length; j++) {
				if (i == 0) { // se � a primeira opera��o
					if (j == 0) { // se � a primeira tarefa
						shifts[0][0] = matrizMaqsOps[solucao[i]][0];

					} else {// se s�o as demais tarefas
						shifts[i][j] = shifts[i][j - 1]
								+ matrizMaqsOps[solucao[i]][j];

					}
				} else { // se s�o as demais opera��es (>0)
					if (j == 0) { // se � a primeira tarefa
						shifts[i][0] = shifts[i - 1][0]
								+ matrizMaqsOps[solucao[i]][0];

					} else { // se s�o as demais tarefas
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
 
