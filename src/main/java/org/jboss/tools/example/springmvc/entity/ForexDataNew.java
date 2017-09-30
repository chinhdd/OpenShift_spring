package org.jboss.tools.example.springmvc.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table
public class ForexDataNew implements Serializable {

   /**
    * Default value
    */
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @NotNull
   @Size(min = 1, max = 20)
   @Pattern(regexp = "[A-Za-z0-9]*", message = "must contain only letters and numbers")
   @Column(name = "forex_name")
   private String forexName;
   
   @NotNull
   @Digits(fraction = 0, integer = 4)
   private Short period;
   
   @NotNull
   @Size(min = 1, max = 20)
   @Column(name = "date_string")
   private String dateString;
   
   @NotNull
   @Column(name = "date_value")
   private Long dateValue;
   
   @NotNull
   private Float open;
   
   @NotNull
   private Float close;
   
   @NotNull
   private Float high;
   
   @NotNull
   private Float low;
   
   public Long getId() {
      return id;
   }
   public void setId(Long id) {
      this.id = id;
   }
   
   public String getForexName() {
      return forexName;
   }
   public void setForexName(String forexName) {
      this.forexName = forexName;
   }
   
   public Short getPeriod() {
      return period;
   }
   public void setPeriod(Short period) {
      this.period = period;
   }
   
   public String getDateString() {
      return dateString;
   }
   public void setDateString(String dateString) {
      this.dateString = dateString;
   }
   
   public Long getDateValue() {
      return dateValue;
   }
   public void setDateValue(Long dateValue) {
      this.dateValue = dateValue;
   }
   
   public Float getOpen() {
      return open;
   }
   public void setOpen(Float open) {
      this.open = open;
   }
   
   public Float getClose() {
      return close;
   }
   public void setClose(Float close) {
      this.close = close;
   }
   
   public Float getHigh() {
      return high;
   }
   public void setHigh(Float high) {
      this.high = high;
   }
   
   public Float getLow() {
      return low;
   }
   public void setLow(Float low) {
      this.low = low;
   }
}
