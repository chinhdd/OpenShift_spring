package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;

import org.jboss.tools.example.springmvc.entity.ForexDataNew;

public class SForexData implements Serializable {

   /** Default value included to remove warning. Remove or modify at will. **/
   private static final long serialVersionUID = 3L;
   
   private String date;
   private Float open;
   private Float close;
   private Float high;
   private Float low;
   
   public String getDate() {
      return date;
   }
   
   public Float getOpen() {
      return open;
   }
   
   public Float getClose() {
      return close;
   }
   
   public Float getHigh() {
      return high;
   }
   
   public Float getLow() {
      return low;
   }
   
   public SForexData() {
      
   }
   
   public SForexData(ForexData data) {
      this.date = String.format("%04d-%02d-%02d %02d:%02d", data.getYear(), data.getMonth(), 
            data.getDay(), data.getHour(), data.getMinute());
      this.open = data.getOpen();
      this.close = data.getClose();
      this.high = data.getHigh();
      this.low = data.getLow();
   }
   
   public SForexData(ForexDataNew data) {
      this.date = data.getDateString();
      this.open = data.getOpen();
      this.close = data.getClose();
      this.high = data.getHigh();
      this.low = data.getLow();
   }
   
   @Override
   public String toString() {
      String result = date + ", ";
      result += "open: " + open + ", ";
      result += "close: " + close + ", ";
      result += "high: " + high + ", ";
      result += "low: " + low;
      return result;
   }
}
