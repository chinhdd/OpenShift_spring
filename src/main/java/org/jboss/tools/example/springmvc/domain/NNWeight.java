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

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table
public class NNWeight implements Serializable {

   /**
    * Default value for serialize
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
   @Digits(fraction = 0, integer = 2)
   private Byte layer;
   
   @NotNull
   @Digits(fraction = 0, integer = 2)
   @Column(name = "neuron_to")
   private Byte neuronTo;
   
   @NotNull
   @Digits(fraction = 0, integer = 2)
   @Column(name = "neuron_from")
   private Byte neuronFrom;
   
   @NotNull
   private Double weight;
   
   public Double getWeight() {
      return weight;
   }
   
   public void setWeight(double weight) {
      this.weight = weight;
   }
}
