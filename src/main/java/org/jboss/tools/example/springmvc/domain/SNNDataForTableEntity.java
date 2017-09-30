package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SNNDataForTableEntity implements Serializable {

   /**
    * Default serialize value
    */
   private static final long serialVersionUID = 1L;

   private List<SNNDataForTable> data;
   private SNNInfo info;
   
   public List<SNNDataForTable> getData() {
      return data;
   }
   
   public SNNInfo getInfo() {
      return info;
   }
   
   public SNNDataForTableEntity(List<SNNDataForTable> data, SNNInfo info) {
      this.data = data;
      this.info = info;
   }
}
