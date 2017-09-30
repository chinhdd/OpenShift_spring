package org.jboss.tools.example.springmvc.entity;

import java.io.Serializable;

public class RequestForexDataEntity implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   private String name;
   private String startDate;
   private String endDate;
   private String startTime;
   private String endTime;
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   
   public String getStartDate() {
      return startDate;
   }
   public void setStartDate(String startDate) {
      this.startDate = startDate;
   }
   
   public String getEndDate() {
      return endDate;
   }
   public void setEndDate(String endDate) {
      this.endDate = endDate;
   }
   
   public String getStartTime() {
      return startTime;
   }
   public void setStartTime(String startTime) {
      this.startTime = startTime;
   }
   
   public String getEndTime() {
      return endTime;
   }
   public void setEndTime(String endTime) {
      this.endTime = endTime;
   }
}
