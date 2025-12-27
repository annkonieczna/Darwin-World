package agh.ics.oop.model;

public final class Genome {

    private Genome() {

    }

    private static int[] combine(int[] genomeA, int[] genomeB, int energyA, int energyB, int minMutations, int MaxMutations) {

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

        double ratio = (double) moreEnergy /(moreEnergy + lessEnergy);
        int strongerGeneCount = (int) Math.round(ratio*length);





    }





}
