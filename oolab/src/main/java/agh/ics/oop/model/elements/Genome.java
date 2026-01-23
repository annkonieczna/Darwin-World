package agh.ics.oop.model.elements;

import java.util.*;
import java.util.stream.IntStream;

public final class Genome {

    private static final Random random = new Random();

    private Genome() {}

    public static List<Integer> generate(int length) {
        return IntStream.range(0,length)
                .map(i -> random.nextInt(8))
                .boxed()
                .toList();
    }

    public static List<Integer> combine(List<Integer> genomeA, List<Integer> genomeB, int energyA, int energyB, int minMutations, int maxMutations) {

        int length = genomeA.size();

        List<Integer> stronger = energyA >= energyB ? genomeA : genomeB;
        List<Integer> weaker = energyA >= energyB ? genomeB : genomeA;

        double ratio = (double) Math.max(energyA,energyB) / (energyA+energyB);
        int strongerGeneCount = (int) Math.round(ratio * length);
        boolean isLeft = random.nextBoolean();
        List<Integer> child = new ArrayList<>();
        if (isLeft) {
            child.addAll(stronger.subList(0,strongerGeneCount));
            child.addAll(weaker.subList(strongerGeneCount, length));

        } else {
            int weakerGeneCount = length - strongerGeneCount;
            child.addAll(weaker.subList(0, weakerGeneCount));
            child.addAll(stronger.subList(weakerGeneCount, length));
        }
        mutate(child, minMutations, maxMutations);
        return List.copyOf(child);
    }

    public static void mutate(List<Integer> child, int min, int max) {
        if (max == 0 || min > max) return;

        int mutations = random.nextInt((max - min + 1)) + min;
        List<Integer> indices = new ArrayList<>(IntStream.range(0,child.size()).boxed().toList());
        Collections.shuffle(indices);
        for (int i = 0; i < mutations; i++) {
            int index = indices.get(i);
            int oldGene = child.get(index);
            int newGene;
            do {
                newGene = random.nextInt(8);
            } while (newGene == oldGene);

            child.set(index,newGene);
        }
    }

    public static int calculateResistanceScore(List<Integer> genome, List<Integer> pattern) {

        int score = 0;

        for (int i = 0; i < genome.size(); i++) {
            if(Objects.equals(genome.get(i), pattern.get(i))) {
                score++;
            }
        }
        return (int) ((score / (double) genome.size() * 100));
    }
}
