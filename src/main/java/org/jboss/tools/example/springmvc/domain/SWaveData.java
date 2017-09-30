package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;
import java.util.List;

public class SWaveData implements Serializable {

   /** Default value included to remove warning. Remove or modify at will. **/
   private static final long serialVersionUID = 1L;
   
   private String content;
   private List<Integer> indexList;
   private List<SForexData> dataList;
   
   public String getContent() {
      return content;
   }
   
   public List<Integer> getIndexList() {
      return indexList;
   }
   
   public List<SForexData> getDataList() {
      return dataList;
   }
   
   public SWaveData() {
      
   }
   
   public SWaveData(String content, List<Integer> indexList, List<SForexData> dataList) {
      this.content = content;
      this.indexList = indexList;
      this.dataList = dataList;
   }
}
