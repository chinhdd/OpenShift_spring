package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SComplexData implements Serializable {

   /**
    * Default value
    */
   private static final long serialVersionUID = 1L;

   private String content;
   private List<SForexData> dataList;
   private List<SUnitData> indexList;
   
   private SWaveStatistics stat;
   
   private SOutputPercent output;
   
   public String getContent() {
      return content;
   }
   
   public List<SForexData> getDataList() {
      return dataList;
   }
   
   public List<SUnitData> getIndexList() {
      return indexList;
   }
   
   public SWaveStatistics getStat() {
      return stat;
   }
   
   public SOutputPercent getOutput() {
      return output;
   }
   
   public SComplexData() {
      
   }
   public SComplexData(String content, List<SForexData> dataList, List<SUnitData> indexList, 
         SWaveStatistics stat, SOutputPercent output) {
      this.content = content;
      this.dataList = dataList;
      this.indexList = indexList;
      this.stat = stat;
      this.output = output;
   }
}
