package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;

public class SOutputPercent implements Serializable {

   /**
    * Default value
    */
   private static final long serialVersionUID = 1L;

   private String largerPercent;
   private String smallerPercent;
   
   public String getLargerPercent() {
      return largerPercent;
   }
   
   public String getSmallerPercent() {
      return smallerPercent;
   }
   
   public SOutputPercent() {
      
   }
   public SOutputPercent(String largerPercent, String smallerPercent) {
      this.largerPercent = largerPercent;
      this.smallerPercent = smallerPercent;
   }
}
