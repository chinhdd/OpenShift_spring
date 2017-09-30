package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SNNData implements Serializable {

   /**
    * Default serialize value
    */
   private static final long serialVersionUID = 1L;

   private List<Double> inputList;
   
   private Double outputValue;
   
   private String startTime;
   
   public List<Double> getInputList() {
      return inputList;
   }
   
   public Double getOutputValue() {
      return outputValue;
   }
   
   public void setOutputValue(Double outputValue) {
      this.outputValue = outputValue;
   }
   
   public String getStartTime() {
      return startTime;
   }
   
   public SNNData() {
      
   }
   public SNNData(List<Double> inputList, Double outputValue, String startTime) {
      this.inputList = inputList;
      this.outputValue = outputValue;
      this.startTime = startTime;
   }
}
