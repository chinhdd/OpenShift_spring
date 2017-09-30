package org.jboss.tools.example.springmvc.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jboss.tools.example.springmvc.domain.ForexData;
import org.jboss.tools.example.springmvc.domain.SComplexData;
import org.jboss.tools.example.springmvc.domain.SForexData;
import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SOutputPercent;
import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.domain.SWaveData;
import org.jboss.tools.example.springmvc.domain.SWaveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ForexDataDaoImpl implements ForexDataDao {
   
   public static final int CONSTANT_INTERVAL = 10;

   @Autowired
   private EntityManager em;
   
   @Override
   public List<ForexData> findAllOrderedByDate(String forexName) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<ForexData> criteria = cb.createQuery(ForexData.class);
      Root<ForexData> forexData = criteria.from(ForexData.class);
      
      criteria.select(forexData).where(cb.equal(forexData.get("forexName"), forexName))
         .orderBy(cb.asc(forexData.get("year")), cb.asc(forexData.get("month")), 
               cb.asc(forexData.get("day")), cb.asc(forexData.get("hour")), cb.asc(forexData.get("minute")));
      return em.createQuery(criteria).getResultList();
   }

   @Override
   public SComplexData getAllDataAndWave(String forexName) {
      //first get ori data from db
      List<ForexData> rawDataList = findAllOrderedByDate(forexName);
      //convert to SForexData
      ArrayList<SForexData> dataList = new ArrayList<SForexData>();
      for (ForexData dataItem : rawDataList) {
         SForexData sForex = new SForexData(dataItem);
         dataList.add(sForex);
      }
      //find wave data
      List<SUnitData> waveUnitList = new ArrayList<SUnitData>();
      //find maximum and minimum
      int[] waveIndex = new int[] { -1, -1, -1 };
      int n = dataList.size();
      for (int i = n - 1; i >= 0; i--) {
         boolean isMin = isMinimum(dataList, i, false);
         if (isMin) {
            isMin = isMinimum(dataList, i, true);
         }
         boolean isMax = isMaximum(dataList, i, false);
         if (isMax) {
            isMax = isMaximum(dataList, i, true);
         }
         if (isMax) {
            waveIndex[1] = i;
         }
         if (isMin) {
            if (waveIndex[2] == -1) {
               waveIndex[2] = i;
               SUnitData unitMin = new SUnitData(i, "buy");
               waveUnitList.add(unitMin);
            } else if (waveIndex[1] <= waveIndex[2] && waveIndex[1] >= i) {
               waveIndex[0] = i;
               //save this wave
               SUnitData unitMax = new SUnitData(waveIndex[1], "sell");
               SUnitData unitMin = new SUnitData(waveIndex[0], "buy");
               //save waveData to result list and clear status
               waveUnitList.add(unitMax);
               waveUnitList.add(unitMin);
               
               waveIndex[2] = waveIndex[0];
               waveIndex[0] = -1;
               waveIndex[1] = -1;
            }
         }
      }
      //now calculate statistics
      List<String> poles = new ArrayList<String>();
      List<Integer> polePercents = new ArrayList<Integer>();
      List<Integer> distances = new ArrayList<Integer>();
      List<Integer> distancePercents = new ArrayList<Integer>();
      
      //init lib statistics
      //SummaryStatistics sumStatPole = new SummaryStatistics();
      //SummaryStatistics sumStatDistance = new SummaryStatistics();
      int maxDistance = 0;
      float maxPole = 0;
      
      //poles from 8th to 3rd in list
      for (int i = 8; i >= 3; i--) {
         float value = (i % 2 == 0 ? dataList.get(waveUnitList.get(i).getIndex()).getLow()
               : dataList.get(waveUnitList.get(i).getIndex()).getHigh());
         poles.add("" + value);
         if (maxPole < value) {
            maxPole = value;
         }
         if (i < 8) {
            int dis = waveUnitList.get(i).getIndex() - waveUnitList.get(i+1).getIndex();
            distances.add(dis);
            if (maxDistance < dis) {
               maxDistance = dis;
            }
         }
      }
      
      //mean and sd
      SummaryStatistics sumStatPole = new SummaryStatistics();
      int startIndex = waveUnitList.get(8).getIndex();
      int endIndex = waveUnitList.get(3).getIndex();
      for (int i = startIndex; i <= endIndex; i++) {
         sumStatPole.addValue(dataList.get(i).getOpen());
         sumStatPole.addValue(dataList.get(i).getClose());
         sumStatPole.addValue(dataList.get(i).getHigh());
         sumStatPole.addValue(dataList.get(i).getLow());
      }
      String mean = String.format("%.5f", sumStatPole.getMean());
      String sd = String.format("%.8f", sumStatPole.getStandardDeviation());
      double dMean = sumStatPole.getMean();
      double dSd = sumStatPole.getStandardDeviation();
      int numSd = 5;
      double dMinMean = dMean - dSd * numSd;
      
      //percent from maximum number
      for (int i = 8; i >= 3; i--) {
         double value = (i % 2 == 0 ? dataList.get(waveUnitList.get(i).getIndex()).getLow()
               : dataList.get(waveUnitList.get(i).getIndex()).getHigh());
         value = (value - dMinMean) / dSd; // expect value will be from 0 to 10
         if (value < 1) {
            value = 1;
         } else if (value > 9) {
            value = 9;
         }
         polePercents.add((int) (value * 10));
         if (i < 8) {
            int dis = waveUnitList.get(i).getIndex() - waveUnitList.get(i+1).getIndex();
            distancePercents.add((int) (dis * 100 / maxDistance));
         }
      }
      
      SWaveStatistics stat = new SWaveStatistics(poles, polePercents, distances, distancePercents, mean, sd);
      
      //output percent
      startIndex = waveUnitList.get(3).getIndex();
      endIndex = waveUnitList.get(1).getIndex();
      int numPositive = 0;
      //double dMean = sumStatPole.getMean();
      for (int i = startIndex; i <= endIndex; i++) {
         if (dMean >= dataList.get(i).getHigh()) {
            numPositive += 4; continue;
         }
         if (dMean < dataList.get(i).getLow()) {
            continue;
         } else {
            numPositive++;
         }
         if (dMean >= dataList.get(i).getOpen()) {
            numPositive++;
         }
         if (dMean >= dataList.get(i).getClose()) {
            numPositive++;
         }
      }
      float fNumPositive = numPositive * 100.0f / ((endIndex + 1 - startIndex) * 4);
      String largerPercent = String.format("%.2f", fNumPositive);
      fNumPositive = 100 - fNumPositive;
      String smallerPercent = String.format("%.2f", fNumPositive);
      SOutputPercent output = new SOutputPercent(smallerPercent, largerPercent);//reverse smaller and larger
      return new SComplexData("waveIncluded", dataList, waveUnitList, stat, output);
   }

   @Override
   public List<SNNData> getNNDataFromForex(String forexName, int period,
         int count) {
      List<SNNData> result = new ArrayList<SNNData>();
      //period TODO
      SComplexData complexData = getAllDataAndWave(forexName);
      List<SUnitData> waveUnitList = complexData.getIndexList();
      List<SForexData> dataList = complexData.getDataList();
      int startPole = 8;
      int endPole = 3;
      int startIndex, endIndex;
      String startTime;
      SummaryStatistics sumStatPole = new SummaryStatistics();
      double dMean;// = sumStatPole.getMean();
      double dSd;// = sumStatPole.getStandardDeviation();
      int numSd = 5;
      double dMinMean;// = dMean - dSd * numSd;
      for (int i = 0; i < count; i++) {
         startPole = 8 + i * 2;
         endPole = 3 + i * 2;
         if (complexData.getIndexList().size() <= startPole) {
            break;
         }
         startIndex = waveUnitList.get(startPole).getIndex();
         endIndex = waveUnitList.get(endPole).getIndex();
         startTime = dataList.get(startIndex).getDate();
         sumStatPole.clear();
         for (int j = startIndex; j <= endIndex; j++) {
            sumStatPole.addValue(dataList.get(j).getOpen());
            sumStatPole.addValue(dataList.get(j).getClose());
            sumStatPole.addValue(dataList.get(j).getHigh());
            sumStatPole.addValue(dataList.get(j).getLow());
         }
         dMean = sumStatPole.getMean();
         dSd = sumStatPole.getStandardDeviation();
         dMinMean = dMean - dSd * numSd;
         List<Double> inputNNList = new ArrayList<Double>();
         for (int j = startPole; j >= endPole; j--) {
            double value = (j % 2 == 0 ? dataList.get(waveUnitList.get(j).getIndex()).getLow()
                  : dataList.get(waveUnitList.get(j).getIndex()).getHigh());
            value = (value - dMinMean) / dSd; // expect value will be from 0 to 10
            if (value < 1) {
               value = 1;
            } else if (value > 9) {
               value = 9;
            }
            inputNNList.add(value / 10);
         }
         //for output NN
         startPole = 3 + i * 2;
         endPole = 1 + i * 2;
         startIndex = waveUnitList.get(startPole).getIndex();
         endIndex = waveUnitList.get(endPole).getIndex();
         int numPositive = 0;
         for (int j = startIndex; j <= endIndex; j++) {
            if (dMean >= dataList.get(j).getHigh()) {
               numPositive += 4; continue;
            }
            if (dMean < dataList.get(j).getLow()) {
               continue;
            } else {
               numPositive++;
            }
            if (dMean >= dataList.get(j).getOpen()) {
               numPositive++;
            }
            if (dMean >= dataList.get(j).getClose()) {
               numPositive++;
            }
         }
         double fNumPositive = numPositive * 1.0 / ((endIndex + 1 - startIndex) * 4);
         Double outputNNValue = 1 - fNumPositive;
         SNNData nnData = new SNNData(inputNNList, outputNNValue, startTime);
         result.add(nnData);
      }
      return result;
   }

   @Override
   public List<ForexData> findByDate(String forexName, int year, int month,
         int day) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<ForexData> findByMonth(String forexName, int year, int month) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<SWaveData> getWaveData(String forexName, int count) {
    //first get ori data from db
      List<ForexData> rawDataList = findAllOrderedByDate(forexName);
      //convert to SForexData
      ArrayList<SForexData> dataList = new ArrayList<SForexData>();
      for (ForexData dataItem : rawDataList) {
         SForexData sForex = new SForexData(dataItem);
         dataList.add(sForex);
      }
      //init
      List<SWaveData> result = new ArrayList<SWaveData>();
      //find maximum and minimum
      int[] waveIndex = new int[] { -1, -1, -1 };
      int n = dataList.size();
      for (int i = n - 1; i >= 0; i--) {
         boolean isMin = isMinimum(dataList, i, false);
         if (isMin) {
            isMin = isMinimum(dataList, i, true);
         }
         boolean isMax = isMaximum(dataList, i, false);
         if (isMax) {
            isMax = isMaximum(dataList, i, true);
         }
         if (isMax) {
            waveIndex[1] = i;
         }
         if (isMin) {
            if (waveIndex[2] == -1) {
               waveIndex[2] = i;
            } else if (waveIndex[1] <= waveIndex[2] && waveIndex[1] >= i) {
               waveIndex[0] = i;
               //save this wave
               List<Integer> indexList = new ArrayList<Integer>();
               indexList.add(0);
               indexList.add(waveIndex[1] - waveIndex[0]);
               indexList.add(waveIndex[2] - waveIndex[0]);
               String content = "min-max-min";
               List<SForexData> subDataList = dataList.subList(waveIndex[0], waveIndex[2] + 1);
               SWaveData waveData = new SWaveData(content, indexList, subDataList);
               //save waveData to result list and clear status
               result.add(waveData);
               if (result.size() == count) {
                  break;
               }
               waveIndex[2] = waveIndex[0];
               waveIndex[0] = -1;
               waveIndex[1] = -1;
            }
         }
      }
      return result;
   }

   @Override
   public List<SWaveData> getWaveData(String forexName) {
      return getWaveData(forexName, 1000);
   }

   private boolean isMinimum(List<SForexData> dataList, int pivot, boolean isLeft) {
      boolean status = true;
      float minPivot = dataList.get(pivot).getLow();
      if (isLeft) {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot - i;
            if (index < 0) {
               status = false; break;
            }
            SForexData dataItem = dataList.get(index);
            if (dataItem.getLow() < minPivot) {
               status = false; break;
            }
         }
      } else {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot + i;
            if (index >= dataList.size()) {
               status = false; break;
            }
            SForexData dataItem = dataList.get(index);
            if (dataItem.getLow() < minPivot) {
               status = false; break;
            }
         }
      }
      return status;
   }
   
   private boolean isMaximum(List<SForexData> dataList, int pivot, boolean isLeft) {
      boolean status = true;
      float maxPivot = dataList.get(pivot).getHigh();
      if (isLeft) {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot - i;
            if (index < 0) {
               status = false; break;
            }
            SForexData dataItem = dataList.get(index);
            if (dataItem.getHigh() > maxPivot) {
               status = false; break;
            }
         }
      } else {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot + i;
            if (index >= dataList.size()) {
               status = false; break;
            }
            SForexData dataItem = dataList.get(index);
            if (dataItem.getHigh() > maxPivot) {
               status = false; break;
            }
         }
      }
      return status;
   }
}
