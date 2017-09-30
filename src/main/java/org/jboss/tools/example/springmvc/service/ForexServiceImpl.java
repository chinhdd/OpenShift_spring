package org.jboss.tools.example.springmvc.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.tools.example.springmvc.dao.ForexDao;
import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForexServiceImpl implements ForexService {

   @Autowired
   ForexDao forexDao;
   
   @Override
   public List<ForexDataNew> getForexDataBetweenDate(String forexName, short period, String startDate,
         String endDate) {
      Short sPeriod = ForexUtils.getShortPeriod(period);
      Date sDate = ForexUtils.getDateByDay(startDate);
      Date eDate = ForexUtils.getDateByDay(endDate);
      return forexDao.getData(forexName, sPeriod, sDate, eDate);
   }

   @Override
   public List<ForexDataNew> getForexDataBetweenTime(String forexName,
         short period, String startDateTime, String endDateTime) {
      Short sPeriod = ForexUtils.getShortPeriod(period);
      Date sDate = ForexUtils.getDateByMinute(startDateTime);
      Date eDate = ForexUtils.getDateByMinute(endDateTime);
      return forexDao.getData(forexName, sPeriod, sDate, eDate);
   }

   @Override
   public List<ForexDataNew> getForexDataSamples(String forexName,
         short period, String startDateTime, int numSamples) {
      Short sPeriod = ForexUtils.getShortPeriod(period);
      Date sDate = ForexUtils.getDateByMinute(startDateTime);
      String endDateTime = ForexUtils.getNextMinute(startDateTime, period * numSamples);
      Date eDate = ForexUtils.getDateByMinute(endDateTime);
      List<ForexDataNew> result = forexDao.getData(forexName, sPeriod, sDate, eDate);
      Calendar cal = Calendar.getInstance();
      while (result.size() < numSamples) {
         sDate = eDate;
         endDateTime = ForexUtils.getNextMinute(endDateTime, period * numSamples);
         eDate = ForexUtils.getDateByMinute(endDateTime);
         List<ForexDataNew> newDataList = forexDao.getData(forexName, sPeriod, sDate, eDate);
         result.addAll(newDataList);
         cal.setTime(eDate);
         int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
         if (dayOfWeek == Calendar.TUESDAY) {
            if (newDataList.size() == 0) {
               break;
            }
         }
      }
      if (result.size() > numSamples) {
         result = result.subList(0, numSamples);
      }
      return result;
   }

   @Override
   public List<SUnitData> getForexWaveIndex(String forexName, short period,
         String startDate, String endDate) {
      List<ForexDataNew> dataList = getForexDataBetweenDate(forexName, period, startDate, endDate);
      return getForexWaveIndex(dataList);
   }

   @Override
   public List<SUnitData> getForexWaveIndex(List<ForexDataNew> dataList) {
      
      //find wave data
      List<SUnitData> waveUnitList = new ArrayList<SUnitData>();
      //find maximum and minimum
      int[] waveIndex = new int[] { -1, -1, -1 };
      int n = dataList.size();
      for (int i = n - 1; i >= 0; i--) {
         boolean isMin = ForexUtils.isMinimum(dataList, i, false);
         if (isMin) {
            isMin = ForexUtils.isMinimum(dataList, i, true);
         }
         boolean isMax = ForexUtils.isMaximum(dataList, i, false);
         if (isMax) {
            isMax = ForexUtils.isMaximum(dataList, i, true);
         }
         if (isMax) {
            waveIndex[1] = i;
         }
         if (isMin) {
            if (waveIndex[2] == -1) {
               waveIndex[2] = i;
               SUnitData unitMin = new SUnitData(i, SUnitData.TYPE_MINIMUM);
               waveUnitList.add(unitMin);
            } else if (waveIndex[1] <= waveIndex[2] && waveIndex[1] >= i) {
               waveIndex[0] = i;
               //save this wave
               SUnitData unitMax = new SUnitData(waveIndex[1], SUnitData.TYPE_MAXIMUM);
               SUnitData unitMin = new SUnitData(waveIndex[0], SUnitData.TYPE_MINIMUM);
               //save waveData to result list and clear status
               waveUnitList.add(unitMax);
               waveUnitList.add(unitMin);
               
               waveIndex[2] = waveIndex[0];
               waveIndex[0] = -1;
               waveIndex[1] = -1;
            }
         }
      }
      int lastMinIndex = n - 1;
      if (waveUnitList.size() > 0) {
         lastMinIndex = waveUnitList.get(0).getIndex();
      }
      for (int i = n - 1; i > lastMinIndex; i--) {
         boolean isMax = ForexUtils.isMaximum(dataList, i, false);
         if (isMax) {
            isMax = ForexUtils.isMaximum(dataList, i, true);
         }
         if (isMax) {
            SUnitData unitMax = new SUnitData(i, SUnitData.TYPE_MAXIMUM);
            waveUnitList.add(0, unitMax);
            break;
         }
      }
      return waveUnitList;
   }

   @Override
   public List<SUnitData> getForexWaveIndexForLearning(
         List<ForexDataNew> dataList, List<SUnitData> waveIndexList, int index) {
      int numWave = waveIndexList.size();
      int cursor = 0;
      if (waveIndexList.get(0).getType().equalsIgnoreCase(SUnitData.TYPE_MINIMUM)) {
         cursor = 1;
      }
      List<SUnitData> result = new ArrayList<SUnitData>();
      if (index == -1) {
         //this case is getting current real data
         for (int i = 0; i <= 5; i++) {
            result.add(waveIndexList.get(cursor + i));
         }
         return result;
      }
      for (int i = 0; i < index; i++) {
         cursor = cursor + 2;
      }
      if (cursor + 7 >= numWave) {
         return result;//return empty list
      }
      for (int i = 0; i <= 7; i++) {
         result.add(waveIndexList.get(cursor + i));
      }
      return result;
   }

}
