package org.jboss.tools.example.springmvc.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;
import org.jboss.tools.example.springmvc.entity.StatEntity;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

   public static final int STANDARD_DISTANCE = 5;
   
   @Override
   public List<Double> getStandardizeFromZeroToOne(List<Double> inputValues, StatEntity stat) {
      double minValue = stat.mean - stat.sd * STANDARD_DISTANCE;
      List<Double> result = new ArrayList<Double>();
      for (Double value : inputValues) {
         double sValue = (value - minValue) / stat.sd;//expect value should be 0 to 10
         if (sValue < 1) {
            sValue = 1;
         } else if (sValue > 9) {
            sValue = 9;
         }
         sValue = sValue / 10; //standardize value from 0.0 to 1.0
         result.add(sValue);
      }
      return result;
   }

   @Override
   public StatEntity getStatValue(List<ForexDataNew> dataList, int beginIndex,
         int endIndex) {
      SummaryStatistics stat = new SummaryStatistics();
      for (int i = beginIndex; i <= endIndex; i++) {
         stat.addValue(dataList.get(i).getOpen());
         stat.addValue(dataList.get(i).getClose());
         stat.addValue(dataList.get(i).getHigh());
         stat.addValue(dataList.get(i).getLow());
      }
      StatEntity se = new StatEntity();
      se.mean = stat.getMean();
      se.sd = stat.getStandardDeviation();
      return se;
   }

   @Override
   public double getOutputLargerRatio(List<ForexDataNew> dataList, int beginIndex,
         int endIndex, double meanValue) {
      int numSmaller = 0;
      for (int i = beginIndex; i <= endIndex; i++) {
         if (meanValue >= dataList.get(i).getHigh()) {
            numSmaller += 4; continue;
         }
         if (meanValue < dataList.get(i).getLow()) {
            continue;
         } else {
            numSmaller++;
         }
         if (meanValue >= dataList.get(i).getOpen()) {
            numSmaller++;
         }
         if (meanValue >= dataList.get(i).getClose()) {
            numSmaller++;
         }
      }
      double fNumSmaller = numSmaller * 1.0 / ((endIndex + 1 - beginIndex) * 4);
      return 1 - fNumSmaller;//get output larger ratio
   }

   @Override
   public double calculateMean(OutputEntity oEntity) {
      NormalDistribution nd = new NormalDistribution();
      double prSmaller = 1 - oEntity.output;
      //limit the probability
      if (prSmaller > 0.95) {
         prSmaller = 0.95;
      } else if (prSmaller < 0.05) {
         prSmaller = 0.05;
      }
      double pivot = nd.inverseCumulativeProbability(prSmaller);
      return oEntity.statEntity.mean - oEntity.statEntity.sd * pivot;
   }

   @Override
   public double calculateMean(OutputEntity oEntity, int code) {
      NormalDistribution nd = new NormalDistribution();
      double prSmaller = 1 - oEntity.output;
      //limit the probability
      if (prSmaller > 0.98) {
         prSmaller = 0.98;
      } else if (prSmaller < 0.02) {
         prSmaller = 0.02;
      }
      double pivot = nd.inverseCumulativeProbability(prSmaller);
      double sd = oEntity.statEntity.sd;
      switch (code) {
      case 1:
         sd = 0.00075;
         break;
      }
      return oEntity.statEntity.mean - sd * pivot;
   }

   @Override
   public double getTrend(double pivot, double mean, double sd) {
      NormalDistribution nd = new NormalDistribution(mean, sd);
      return 1 - nd.cumulativeProbability(pivot);
   }

   @Override
   public double getTrend(List<ForexDataNew> dataList, double pivot) {
      int numUp = 0;
      int numDown = 0;
      for (ForexDataNew data : dataList) {
         if (data.getOpen().doubleValue() >= pivot) {
            numUp++;
         } else {
            numDown++;
         }
         if (data.getHigh().doubleValue() >= pivot) {
            numUp++;
         } else {
            numDown++;
         }
         if (data.getLow().doubleValue() >= pivot) {
            numUp++;
         } else {
            numDown++;
         }
         if (data.getClose().doubleValue() >= pivot) {
            numUp++;
         } else {
            numDown++;
         }
      }
      double sum = numUp + numDown;
      return numUp / sum;
   }

   @Override
   public double adjustOutput(List<Double> data, double output, int code) {
      switch (code) {
      case 1:
         if (ForexUtils.getMaxIndex(data) == 1 && output < 0.5) {
            if (data.get(2) < data.get(4) && data.get(3) < data.get(5)) {
               return 0.6 + output * 0.1;
            }
         } else if (output > 0.8) {
            if (data.get(0) < data.get(1) && data.get(1) < 0.5) {
               if (data.get(3) > 0.61 && data.get(5) > 0.7) {
                  return 0.9 + output * 0.05;
               } else if (data.get(1) < 0.4) {
                  if (data.get(3) < 0.64 && data.get(5) < data.get(3)) {
                     return 0.5 + output * 0.05;
                  }
               }
            }
         }
         break;
      }
      return output;
   }

   @Override
   public boolean isMaxExisting(List<ForexDataNew> dataList,
         int beginIndex, double maxValue) {
      int endIndex = dataList.size() - 1;
      for (int i = beginIndex; i <= endIndex; i++) {
         if (dataList.get(i).getHigh() > maxValue) {
            return true;
         }
      }
      return false;
   }

   @Override
   public double adjustOutputHigher(double output, StatEntity sEntity, int code) {
      double result = 0;
      switch (code) {
      case 1:
//         if (output < 0.2 && sEntity.sd < 0.0003) {
//            result = output + 0.6;
//         } else {
//            result = output + 0.35;
//         }
         //if (result > 0.98) {
            result = 0.98;
         //}
         break;
      }
      return result;
   }

   @Override
   public double adjustOutputOverHigher(List<ForexDataNew> dataList,
         int beginIndex, double output, StatEntity sEntity, int code) {
      switch (code) {
      case 1:
         if (output > 0.979) {
            OutputEntity oEntity = new OutputEntity(output, sEntity, 0);
            double newMean = calculateMean(oEntity, code);
            //check there are close value higher than newMean
            double maxCloseValue = dataList.get(beginIndex).getClose();
            for (int i = beginIndex + 1; i < dataList.size(); i++) {
               if (dataList.get(i).getClose() > maxCloseValue) {
                  maxCloseValue = dataList.get(i).getClose();
               }
            }
            if (maxCloseValue > newMean) {
               sEntity.mean = maxCloseValue;
               return output;
            }
         }
         break;
      }
      return output;
   }

   @Override
   public double adjustOutputOverLower(List<ForexDataNew> dataList,
         int beginIndex, double output, StatEntity sEntity, int code) {
      switch (code) {
      case 1:
         if (output > 0.97 && output < 0.975) {
            OutputEntity oEntity = new OutputEntity(output, sEntity, 0);
            double newMean = calculateMean(oEntity, code);
            double minCloseValue = dataList.get(beginIndex).getClose();
            for (int i = beginIndex + 1; i < dataList.size(); i++) {
               if (dataList.get(i).getClose() < minCloseValue) {
                  minCloseValue = dataList.get(i).getClose();
               }
            }
            double trend = getTrend(minCloseValue, newMean, sEntity.sd);
            if (trend > 0.99) {
               sEntity.mean = minCloseValue;
               output = 0.02 + output * 0.05;
               //check if there are so many little signal
//               boolean status = true;
//               for (int i = 0; i < 5; i++) {
//                  ForexDataNew forex = dataList.get(dataList.size() - i - 1);
//                  if (Math.abs(forex.getClose() - forex.getOpen()) > 0.0005) {
//                     status = false; break;
//                  }
//               }
               //if (status) {
                  //output = 0.97;
               //}
               return output;
            }
         }
         break;
      }
      return output;
   }

   @Override
   public double checkModelHamerUp(List<ForexDataNew> dataList,
         int index, double output) {
      boolean status = false;
      ForexDataNew forexData = dataList.get(index);
      double distance = forexData.getClose() - forexData.getOpen();
      forexData = dataList.get(index - 1);
      double preDistance = forexData.getClose() - forexData.getOpen();
      if (distance > 0 && preDistance + distance < 0) {
         forexData = dataList.get(index + 1);
         double[] nextDistance = new double[4];
         nextDistance[0] = Math.abs(forexData.getClose() - forexData.getOpen());
         forexData = dataList.get(index + 2);
         nextDistance[1] = Math.abs(forexData.getClose() - forexData.getOpen());
         forexData = dataList.get(index + 3);
         nextDistance[2] = Math.abs(forexData.getClose() - forexData.getOpen());
         //forexData = dataList.get(index + 4);
         //nextDistance[3] = Math.abs(forexData.getClose() - forexData.getOpen());
         if (distance > nextDistance[0] + nextDistance[1] + nextDistance[2]) {
            status = true;
         }
      }
      if (status) {
         output = 0.68 + output * 0.05;
         if (output > 0.95) {
            output = 0.95;
         }
      }
      return output;
   }

}
