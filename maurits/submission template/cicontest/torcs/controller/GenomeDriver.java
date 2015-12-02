package cicontest.torcs.controller;

import cicontest.torcs.genome.IGenome;

public abstract class GenomeDriver
  extends Driver
{
  public abstract void init();
  
  public abstract void loadGenome(IGenome paramIGenome);
  
  public abstract void loadBestGenome();
}