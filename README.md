# Darwin World

A project created as part of the **Object-Oriented Programming** course.

## Description

Darwin World is an evolutionary simulation of a world inhabited by animals and plants.  
Animals (Capibaras) move around the map, gain energy, reproduce, and pass their genes on to their offsprings.  
Over many iterations, the simulation makes it possible to observe changes in the population and the evolution of behaviors resulting from the animals’ genotypes.

The program was implemented in **Java** using **JavaFX** for visualization.


## Project Variant

**Poisons and Resistance**

Some fields on the map generate toxic plants that decrease the animals’ energy.  
However, animals can develop genetic tolerance to poison depending on how closely their genotype matches a specific gene sequence.

## Features 

### Core Features

- configurable world simulation (map size, number of animals, energy parameters, etc.)
- animated visualization of the simulation
- ability to pause and resume the simulation
- real-time display of world statistics:
  - number of animals
  - number of plants
  - number of free fields
  - dominant genotypes
  - average energy level
  - average lifespan
  - average number of children

### Implemented Extensions

- running multiple simulations simultaneously in separate windows
- real-time visualization of animal energy
- saving and loading simulation configurations
- highlighting animals with the dominant genotype
- saving simulation statistics to a CSV file
- visualizing selected statistics on a chart
- dynamic map scaling depending on the world size

## Additional Elements

- two map rendering modes depending on field size
- visual highlighting of the most common plant spawn areas
- additional marking of toxic plants spawn areas with an orange gradient
- animals with the dominant genotype are highlighted in blue
- when multiple animals occupy the same field, they are graphically represented by three capibaras 

## Running the Project

1. Clone the repository
2. Open the project in an IDE (for example, IntelliJ IDEA)
3. Run the `WorldGUI` class
4. Choose the parameters you want and click **Start**

## Authors

- Anna Konieczna  
- Mateusz Śliwa