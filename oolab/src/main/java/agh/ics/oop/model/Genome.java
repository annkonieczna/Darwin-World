package agh.ics.oop.model;

import java.util.Random;

public final class Genome {

    private static final Random random = new Random();

    private Genome() {

    }

    public static int[] combine(int[] genomeA, int[] genomeB, int energyA, int energyB, int minMutations, int maxMutations) {

        int length = genomeA.length;

        int[] stronger;
        int[] weaker;
        int moreEnergy;
        int lessEnergy;
        if (energyA >= energyB) {
            moreEnergy = energyA;
            lessEnergy = energyB;
            stronger = genomeA;
            weaker = genomeB;

        } else {
            stronger = genomeB;
            weaker = genomeA;
            moreEnergy = energyB;
            lessEnergy = energyA;
        }

        double ratio = (double) moreEnergy / (moreEnergy + lessEnergy);
        int strongerGeneCount = (int) Math.round(ratio * length);
        boolean isLeft = random.nextBoolean();
        int[] child = new int[length];
        if (isLeft) {
            System.arraycopy(stronger, 0, child, 0, strongerGeneCount);
            System.arraycopy(weaker, strongerGeneCount, child, strongerGeneCount, length - strongerGeneCount);

        } else {
            int weakerGeneCount = length - strongerGeneCount;
            System.arraycopy(weaker, 0, child, 0, weakerGeneCount);
            System.arraycopy(stronger, weakerGeneCount, child, weakerGeneCount, strongerGeneCount);
        }
        mutate(child, minMutations, maxMutations);
        return child;


    }

    public static void mutate(int[] child, int min, int max) {
        if (max == 0) return;

        int mutations = random.nextInt((max - min + 1)) + min;
        for (int i = 0; i < mutations; i++) {
            int index = random.nextInt(child.length);
            int newGene;
            do {
                newGene = random.nextInt(8);
            } while (newGene == child[index]);

            child[index] = newGene;


        }

    }


}
