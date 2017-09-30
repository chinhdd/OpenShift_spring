package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SNNInfo implements Serializable {

   /**
    * Default serialize value
    */
   private static final long serialVersionUID = 1L;

   private transient List<String> actualOutput;
   
   private List<Float> networkError;
   
   public List<String> getActualOutput() {
      return actualOutput;
   }
   
   public List<Float> getNetworkError() {
      return networkError;
   }
   
   public SNNInfo(List<String> actualOutput, List<Float> networkError) {
      this.actualOutput = actualOutput;
      this.networkError = networkError;
   }
}
