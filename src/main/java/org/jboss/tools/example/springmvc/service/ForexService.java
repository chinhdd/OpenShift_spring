package org.jboss.tools.example.springmvc.service;

import java.util.List;

import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;

public interface ForexService {

   public List<ForexDataNew> getForexDataBetweenDate(String forexName, short period, String startDate, String endDate);
   
   public List<ForexDataNew> getForexDataBetweenTime(String forexName, short period, String startDateTime, String endDateTime);
   
   public List<ForexDataNew> getForexDataSamples(String forexName, short period, String startDateTime, int numSamples);
   
   public List<SUnitData> getForexWaveIndex(String forexName, short period, String startDate, String endDate);
   
   public List<SUnitData> getForexWaveIndex(List<ForexDataNew> dataList);
   
   public List<SUnitData> getForexWaveIndexForLearning(List<ForexDataNew> dataList, List<SUnitData> waveIndexList, int index);
}
