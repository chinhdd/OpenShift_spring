package org.jboss.tools.example.springmvc.repo;

import java.util.List;

import org.jboss.tools.example.springmvc.domain.ForexData;
import org.jboss.tools.example.springmvc.domain.SComplexData;
import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SWaveData;

public interface ForexDataDao {

   public List<ForexData> findAllOrderedByDate(String forexName);
   
   public List<ForexData> findByDate(String forexName, int year, int month, int day);
   
   public List<ForexData> findByMonth(String forexName, int year, int month);
   
   public List<SWaveData> getWaveData(String forexName, int count);
   
   public List<SWaveData> getWaveData(String forexName);
   
   public SComplexData getAllDataAndWave(String forexName);
   
   public List<SNNData> getNNDataFromForex(String forexName, int period, int count);
}
