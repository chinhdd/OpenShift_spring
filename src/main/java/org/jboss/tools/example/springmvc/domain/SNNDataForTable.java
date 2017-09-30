package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SNNDataForTable implements Serializable {

   /**
    * Default serialize value
    */
   private static final long serialVersionUID = 1L;

   private Integer id;
   private String startTime;
   private String inputOne;
   private String inputTwo;
   private String inputThree;
   private String inputFour;
   private String inputFive;
   private String inputSix;
   private String output;
   private String actualOutput;
   
   public Integer getId() {
      return id;
   }
   public String getStartTime() {
      return startTime;
   }
   public String getInputOne() {
      return inputOne;
   }
   public String getInputTwo() {
      return inputTwo;
   }
   public String getInputThree() {
      return inputThree;
   }
   public String getInputFour() {
      return inputFour;
   }
   public String getInputFive() {
      return inputFive;
   }
   public String getInputSix() {
      return inputSix;
   }
   public String getOutput() {
      return output;
   }
   public String getActualOutput() {
      return actualOutput;
   }
   
   public SNNDataForTable(int id, SNNData nnData) {
      this.id = id;
      this.startTime = nnData.getStartTime();
      List<Double> inputList = nnData.getInputList();
      inputOne = String.format("%.5f", inputList.get(0));
      inputTwo = String.format("%.5f", inputList.get(1));
      inputThree = String.format("%.5f", inputList.get(2));
      inputFour = String.format("%.5f", inputList.get(3));
      inputFive = String.format("%.5f", inputList.get(4));
      inputSix = String.format("%.5f", inputList.get(5));
      output = String.format("%.5f", nnData.getOutputValue());
   }
   
   public void setActualOutput(String actualOutput) {
      this.actualOutput = actualOutput;
   }
}
