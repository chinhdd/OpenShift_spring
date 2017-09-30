package org.jboss.tools.example.springmvc.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table
public class ForexData implements Serializable {

   /** Default value included to remove warning. Remove or modify at will. **/
   private static final long serialVersionUID = 2L;

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
   @Digits(fraction = 0, integer = 4)
   private Short year;

   @NotNull
   @Digits(fraction = 0, integer = 2)
   private Byte month;
   
   @NotNull
   @Digits(fraction = 0, integer = 2)
   private Byte day;
   
   @NotNull
   @Digits(fraction = 0, integer = 2)
   private Byte hour;
   
   @NotNull
   @Digits(fraction = 0, integer = 2)
   private Byte minute;
   
   @NotNull
   private Float ma;
   
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
   
   public Short getYear() {
      return year;
   }
   public void setYear(Short year) {
      this.year = year;
   }
   
   public Byte getMonth() {
      return month;
   }
   public void setMonth(Byte month) {
      this.month = month;
   }
   
   public Byte getDay() {
      return day;
   }
   public void setDay(Byte day) {
      this.day = day;
   }
   
   public Byte getHour() {
      return hour;
   }
   public void setHour(Byte hour) {
      this.hour = hour;
   }
   
   public Byte getMinute() {
      return minute;
   }
   public void setMinute(Byte minute) {
      this.minute = minute;
   }
   
   public Float getMa() {
      return ma;
   }
   public void setMa(Float ma) {
      this.ma = ma;
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
