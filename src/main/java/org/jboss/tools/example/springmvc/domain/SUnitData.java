package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;

public class SUnitData implements Serializable {
   
   public static final String TYPE_MINIMUM = "buy";
   public static final String TYPE_MAXIMUM = "sell";

   /**
    * Default serial value
    */
   private static final long serialVersionUID = 1L;

   private Integer index;
   private String type;
   
   public Integer getIndex() {
      return index;
   }
   
   public String getType() {
      return type;
   }
   
   public SUnitData() {
      
   }
   public SUnitData(int index, String type) {
      this.index = index;
      this.type = type;
   }
}
