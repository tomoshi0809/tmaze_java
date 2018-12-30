import java.util.Random;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class EvolutionarySearch {
	final double CROSS_RATE = 0.1;
	final double MUTATE_RATE = 0.1;
	final double SIGMA = 0.3;
	final int NUM_THREAD = 6;
	Environment env;
	Phenotype p;
	int numInputs;
	int numNeurons;
	int numPops;
	int numGroup;
	Genotype[] pop;
	int numEval;
	Class envCls;
	Class pheCls;
	Random rand;

	EvolutionarySearch(Class envCls, Class pheCls, int numNeurons, int numPops, int numGroup) {
		try {
			Class envClass = Class.forName(envCls.getName());
			this.env = (Environment) envClass.newInstance();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(0);
		}
		this.pheCls = pheCls;
		this.numInputs = env.numInputs;
		this.numNeurons = numNeurons;
		this.numPops = numPops;
		this.numGroup = numGroup;
		this.pop = new Genotype[numPops];
		for (int i = 0; i < numPops; i++) {
			this.pop[i] = new Genotype(numInputs, numNeurons);
		}
		this.numEval = 2;
		this.envCls = envCls;
		this.pheCls = pheCls;
		this.rand = new Random();
	}

	public void run(int numGenerations, boolean printStats) {
		for (int i = 0; i < numGenerations; i++) {
			evaluate(this.pop, this.numEval);
			if (printStats) {
				double[] stats = fitStats(this.pop);
				System.out.print(i + ", ");
				for (int j = 0; j < stats.length; j++) {
					System.out.print(stats[j] + ", ");
				}
				System.out.println();
			}
			if (i == numGenerations - 1) {
				break;
			}
			Genotype [] selected = select(this.pop, this.numPops, this.numGroup);
			Genotype [] crossovered = crossover(selected, CROSS_RATE);
			this.pop = cleanFitness(mutate(crossovered, MUTATE_RATE, SIGMA));
		}
	}

	void evaluate(Genotype[] pop, int numEval) {
		int npop = pop.length;
		int nperthread = npop / NUM_THREAD;
		int thidx = 0;
		
		Thread [] th = new Thread[NUM_THREAD];
		
		while (thidx < NUM_THREAD) {
			int start = nperthread * thidx;
			int end = nperthread * (thidx + 1);
			if (thidx == NUM_THREAD - 1) {
				end = npop;
			}
			Genotype [] unit = new Genotype[end - start];
			for (int i = 0; i < (end - start); i ++) {
				unit[i] = pop[start + i];
			}
			th [thidx] = new EvaluateThread(this.env, unit, this.numEval, this.rand);
			th[thidx].start();
			thidx ++;
		}
		
		for (thidx = 0; thidx < NUM_THREAD; thidx ++) {
			try {	
				th[thidx].join();
			} catch (InterruptedException e) {
				System.err.println(e);
				System.exit(1);
			}
		}
	}

	double[] fitStats(Genotype[] pop) {
		double[] fits = new double[pop.length];
		for (int i = 0; i < pop.length; i++) {
			fits[i] = pop[i].fitness;
		}
		StandardDeviation std = new StandardDeviation(false);
		double[] ret = { StatUtils.mean(fits), StatUtils.percentile(fits, 50), std.evaluate(fits),
				StatUtils.max(fits), StatUtils.min(fits) };
		return ret;
	}

	Genotype[] select(Genotype[] pop, int numPop, int numGroup) {
		if (pop.length == 0) {
			System.err.println("EvolutionarySearch - select () receives an empty pop");
		}
		Genotype[] ret = new Genotype[numPop];
		int offset = (int) (Math.random() * numPop);
		for (int count = 0; count < (numPop / numGroup); count ++) {
			int head = (offset + numGroup * count) % numPop;
			Genotype [] group = new Genotype[numGroup];
			for (int index = head; index < head + numGroup; index ++) {
				group[index - head] = pop[index % numPop];
			}
			Genotype groupBest = getBest(group);
			ret[head] = pop[head];
			for (int index = head + 1; index < head + numGroup; index ++) {
				ret[index % numPop] = Genotype.copy(groupBest);
			}
		}
		return ret;
	}

	Genotype[] crossover(Genotype[] pop, double cRate) {
		Genotype [] ret = new Genotype[pop.length];
		Genotype best = getBest(pop);
		for (int index = 0; index < pop.length; index ++) {
			if (pop[index] != best && Math.random() < cRate) {
				Genotype partner = pop[(int)(Math.random() * pop.length)];
				Genotype child = Genotype.crossGenotype(pop[index], partner);
				ret[index] = child;
			} else {
				ret[index] = pop[index];
			}
		}
		return ret;
	}

	Genotype[] mutate(Genotype[] pop, double mRate, double sigma) {
		Genotype best = getBest(pop);
		for (int index = 0; index < pop.length; index ++) {
			pop[index] = Genotype.mutateGenotype(pop[index], mRate, sigma, this.rand);
		}
		return pop;
	}

	Genotype[] cleanFitness(Genotype[] pop) {
		for (int index = 0; index < pop.length; index ++) {
			pop[index].fitness = 0;
		}
		return pop;
	}

	Genotype getBest(Genotype [] group) {
		if (group.length == 0) {
			System.err.println("EvolutionarySearch - getBest() receives an empty group");
		}
		double max = 0;
		int index = 0;
		for (int i = 0; i < group.length; i ++) {
			if (group[i].fitness >= max) {
				max = group[i].fitness;
				index = i;
			}
		}
		return group[index];
	}
}
