package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SWaveStatistics implements Serializable {

   /**
    * Default value
    */
   private static final long serialVersionUID = 1L;

   private List<String> poles;
   private List<Integer> polePercents;
   
   private List<Integer> distances;
   private List<Integer> distancePercents;
   
   private String mean;
   private String sd;
   
   public List<String> getPoles() {
      return poles;
   }
   
   public List<Integer> getPolePercents() {
      return polePercents;
   }
   
   public List<Integer> getDistances() {
      return distances;
   }
   
   public List<Integer> getDistancePercents() {
      return distancePercents;
   }
   
   public String getMean() {
      return mean;
   }
   
   public String getSd() {
      return sd;
   }
   
   public SWaveStatistics() {
      
   }
   public SWaveStatistics(List<String> poles, List<Integer> polePercents, List<Integer> distances, 
         List<Integer> distancePercents, String mean, String sd) {
      this.poles = poles;
      this.polePercents = polePercents;
      this.distances = distances;
      this.distancePercents = distancePercents;
      this.mean = mean;
      this.sd = sd;
   }
}
