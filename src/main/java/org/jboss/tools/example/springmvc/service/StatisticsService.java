package org.jboss.tools.example.springmvc.service;

import java.util.List;

import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;
import org.jboss.tools.example.springmvc.entity.StatEntity;

public interface StatisticsService {

   public List<Double> getStandardizeFromZeroToOne(List<Double> inputValues, StatEntity stat);
   
   public StatEntity getStatValue(List<ForexDataNew> dataList, int beginIndex, int endIndex);
   
   public double getOutputLargerRatio(List<ForexDataNew> dataList, int beginIndex, int endIndex, double meanValue);
   
   public double calculateMean(OutputEntity oEntity);
   
   public double calculateMean(OutputEntity oEntity, int code);
   
   public double getTrend(double pivot, double mean, double sd);
   
   public double getTrend(List<ForexDataNew> dataList, double pivot);
   
   public double adjustOutput(List<Double> data, double output, int code);
   
   public boolean isMaxExisting(List<ForexDataNew> dataList, int beginIndex, double maxValue);
   
   public double adjustOutputHigher(double output, StatEntity sEntity, int code);
   
   public double adjustOutputOverHigher(List<ForexDataNew> dataList, int beginIndex, double output, StatEntity sEntity, int code);
   
   public double checkModelHamerUp(List<ForexDataNew> dataList, int index, double output);
   
   public double adjustOutputOverLower(List<ForexDataNew> dataList, int beginIndex, double output, StatEntity sEntity, int code);
}
