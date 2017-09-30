package org.jboss.tools.example.springmvc.entity;

public class OutputEntity {
   public double output;
   public StatEntity statEntity;
   public double realMean;
   
   public OutputEntity(double output, StatEntity statEntity, double realMean) {
      this.output = output;
      this.statEntity = statEntity;
      this.realMean = realMean;
   }
}
