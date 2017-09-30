package org.jboss.tools.example.springmvc.test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.jboss.tools.example.springmvc.dao.ForexDao;
import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;
import org.jboss.tools.example.springmvc.entity.StatEntity;
import org.jboss.tools.example.springmvc.service.ForexService;
import org.jboss.tools.example.springmvc.service.LearningService;
import org.jboss.tools.example.springmvc.service.NNService;
import org.jboss.tools.example.springmvc.service.StatisticsService;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml",
      "classpath:/META-INF/spring/applicationContext.xml" })
@Transactional
public class ForexTest {

   @Autowired
   private ForexDao forexDao;
   
   @Autowired
   private ForexService forexService;
   
   @Autowired
   private StatisticsService statService;
   
   @Autowired
   private NNService nnService;
   
   @Autowired
   private LearningService learningService;
   
   @Test
   public void testFindForex() {
      Date startDate = ForexUtils.getDateByDay("2016-04-18");
      Date endDate = ForexUtils.getDateByDay("2016-04-19");
      List<ForexDataNew> listForexData = forexDao.getData("EURUSD", (short)5, startDate, endDate);
      Assert.assertEquals(true, listForexData.size() > 0);
   }
   
   @Test
   public void testWaveData() {
      String startDateStr = "2016-04-14";
      String endDateStr = "2016-04-23";
      List<SUnitData> listWave = forexService.getForexWaveIndex("EURUSD", (short)5, startDateStr, endDateStr);
      Assert.assertEquals(true, listWave.size() == 108);
   }
   
   @Test
   public void testWaveDataIndexForLearning() {
      String startDateStr = "2016-04-14";
      String endDateStr = "2016-04-19";
      List<ForexDataNew> dataList = forexService.getForexDataBetweenDate("EURUSD", (short)5, startDateStr, endDateStr);
      List<SUnitData> waveList = forexService.getForexWaveIndex(dataList);
      List<SUnitData> firstWaveList = forexService.getForexWaveIndexForLearning(dataList, waveList, 0);
      Assert.assertEquals(8, firstWaveList.size());
      int secondMaxPeak = firstWaveList.get(2).getIndex();
      List<SUnitData> secondWaveList = forexService.getForexWaveIndexForLearning(dataList, waveList, 1);
      Assert.assertEquals(8, secondWaveList.size());
      Assert.assertEquals(secondMaxPeak, secondWaveList.get(0).getIndex().intValue());
      
      //now testing stat service
      int beginIndex = secondWaveList.get(7).getIndex();
      int endIndex = secondWaveList.get(2).getIndex();
      StatEntity sEntity = statService.getStatValue(dataList, beginIndex, endIndex);
      Assert.assertEquals(true, sEntity.mean > 1.0);
      
      //standardize input value for learning
      List<Double> inputLearning = new ArrayList<Double>();
      for (int i = 7; i >= 2; i--) {
         double value = (i % 2 == 0 ? dataList.get(secondWaveList.get(i).getIndex()).getHigh()
               : dataList.get(secondWaveList.get(i).getIndex()).getLow());
         inputLearning.add(value);
      }
      List<Double> standardInputValues = statService.getStandardizeFromZeroToOne(inputLearning, sEntity);
      Assert.assertEquals(6, standardInputValues.size());
      
      //get output larger ratio for output learning
      beginIndex = secondWaveList.get(2).getIndex();
      endIndex = secondWaveList.get(0).getIndex();
      double outputForLearning = statService.getOutputLargerRatio(dataList, beginIndex, endIndex, sEntity.mean);
      Assert.assertEquals(true, outputForLearning <= 1.0);
      
      //now start learning
      List<SNNData> nnDataForLearningList = new ArrayList<SNNData>();
      nnDataForLearningList.add(new SNNData(standardInputValues, outputForLearning, null));
      
      for (int i = 2; i <= 10; i++) {
         List<SUnitData> dataWave = forexService.getForexWaveIndexForLearning(dataList, waveList, i);
         beginIndex = dataWave.get(7).getIndex();
         endIndex = dataWave.get(2).getIndex();
         sEntity = statService.getStatValue(dataList, beginIndex, endIndex);
         //standardize input value for learning
         inputLearning = new ArrayList<Double>();
         for (int j = 7; j >= 2; j--) {
            double value = (j % 2 == 0 ? dataList.get(dataWave.get(j).getIndex()).getHigh()
                  : dataList.get(dataWave.get(j).getIndex()).getLow());
            inputLearning.add(value);
         }
         standardInputValues = statService.getStandardizeFromZeroToOne(inputLearning, sEntity);
         //get output value
         beginIndex = dataWave.get(2).getIndex();
         endIndex = dataWave.get(0).getIndex();
         outputForLearning = statService.getOutputLargerRatio(dataList, beginIndex, endIndex, sEntity.mean);
         nnDataForLearningList.add(new SNNData(standardInputValues, outputForLearning, null));
      }
      
      Assert.assertEquals(true, nnService.learningForex("EURUSD", 5, nnDataForLearningList));
      
      //now learning one by one
      for (int i = 11; i < 30; i++) {
         nnDataForLearningList.clear();
         List<SUnitData> dataWave = forexService.getForexWaveIndexForLearning(dataList, waveList, i);
         if (dataWave.isEmpty()) {
            break;
         }
         beginIndex = dataWave.get(7).getIndex();
         endIndex = dataWave.get(2).getIndex();
         sEntity = statService.getStatValue(dataList, beginIndex, endIndex);
         //standardize input value for learning
         inputLearning = new ArrayList<Double>();
         for (int j = 7; j >= 2; j--) {
            double value = (j % 2 == 0 ? dataList.get(dataWave.get(j).getIndex()).getHigh()
                  : dataList.get(dataWave.get(j).getIndex()).getLow());
            inputLearning.add(value);
         }
         standardInputValues = statService.getStandardizeFromZeroToOne(inputLearning, sEntity);
         //get output value
         beginIndex = dataWave.get(2).getIndex();
         endIndex = dataWave.get(0).getIndex();
         outputForLearning = statService.getOutputLargerRatio(dataList, beginIndex, endIndex, sEntity.mean);
         nnDataForLearningList.add(new SNNData(standardInputValues, outputForLearning, null));
         Assert.assertEquals(true, nnService.learningForex("EURUSD", 5, nnDataForLearningList));
      }
      Assert.assertEquals(true, true);
   }
   
   @Test
   public void testLearningService() {
      int numRight = 0;
      int numWrong = 0;
      int numSamples = 25;
      int code = ForexUtils.getCode("EURUSD");
      String startDateStr = "2016-04-14 00:00";
      String endDateStr = "2016-04-18 09:00";
      //String endDateStr = "2016-04-20 16:00";
      List<ForexDataNew> forexDataList = forexService.getForexDataBetweenTime("EURUSD", (short)5, startDateStr, endDateStr);
      OutputEntity oEntity = learningService.learningAndSave("EURUSD", (short)5, forexDataList);
      
      double newMean = statService.calculateMean(oEntity, code);
      
      double pivot = forexDataList.get(forexDataList.size() - 1).getClose();
      double trend = statService.getTrend(pivot, newMean, oEntity.statEntity.sd);
      Assert.assertEquals(true, trend >= 0 && trend <= 1);
      String lastUpdatedTime = endDateStr;
      
      List<ForexDataNew> futureDataList = forexService.getForexDataSamples("EURUSD", (short)5, lastUpdatedTime, numSamples);
      Assert.assertEquals(numSamples, futureDataList.size());
      
      double actualTrend = statService.getTrend(futureDataList, pivot);
      Assert.assertEquals(true, actualTrend >= 0 && actualTrend <= 1);
      
      if (trend >= 0.5 && actualTrend >= 0.5) {
          numRight++;
      } else if (trend < 0.5 && actualTrend < 0.5) {
          numRight++;
      } else {
          numWrong++;
      }
      System.out.println("numRight = " + numRight + ", numWrong = " + numWrong + ", time " + lastUpdatedTime);
      
      for (int i = 1; i <= 3000; i++) {
         String nextUpdateTime = ForexUtils.getNextMinute(lastUpdatedTime, 5);
         List<ForexDataNew> newDataList = forexService.getForexDataBetweenTime("EURUSD", (short)5, lastUpdatedTime, nextUpdateTime);
         if (newDataList.size() == 0) {
             lastUpdatedTime = nextUpdateTime;
             continue;
         }
         lastUpdatedTime = nextUpdateTime;
         if (lastUpdatedTime.equalsIgnoreCase("2016-04-20 16:30")) {
            int x = 1;
            System.out.println(x);
         }
         Assert.assertEquals(1, newDataList.size());
         forexDataList.addAll(newDataList);
         oEntity = learningService.learningAndSave("EURUSD", (short)5, forexDataList);
         
         newMean = statService.calculateMean(oEntity, code);
         
         pivot = newDataList.get(0).getClose();
         trend = statService.getTrend(pivot, newMean, oEntity.statEntity.sd);
         
         futureDataList = forexService.getForexDataSamples("EURUSD", (short)5, lastUpdatedTime, numSamples);
         if (futureDataList.size() < numSamples) {
            break;
         }
         Assert.assertEquals(numSamples, futureDataList.size());
         actualTrend = statService.getTrend(futureDataList, pivot);
         Assert.assertEquals(true, actualTrend >= 0 && actualTrend <= 1);
         
         if (trend >= 0.5 && actualTrend >= 0.5) {
            numRight++;
         } else if (trend < 0.5 && actualTrend < 0.5) {
            numRight++;
         } else {
            numWrong++;
         }
         System.out.println("numRight = " + numRight + ", numWrong = " + numWrong + ", time " + lastUpdatedTime);
      }
      
      //Assert.assertEquals(true, numRight > numWrong);
      System.out.println(numRight);
      System.out.println(numWrong);
      System.out.println("Percent: " + (numRight * 100.0f / (numRight + numWrong)));
   }
   
   @Test
   public void testTAIEXnotNull() {
      int numRight = 0;
      int numWrong = 0;
      int numSamples = 25;
      int code = ForexUtils.getCode("TAIEX");
      short period = ForexUtils.getPeriod(code);
      String startDateStr = "2002-01-01 01:00";
      String endDateStr = "2004-02-28 01:00";
      //String endDateStr = "2016-04-20 16:00";
      List<ForexDataNew> forexDataList = forexService.getForexDataBetweenTime("TAIEX", period, startDateStr, endDateStr);
      OutputEntity oEntity = learningService.learningAndSave("TAIEX", period, forexDataList);
      
      double newMean = statService.calculateMean(oEntity, code);
      
      double pivot = forexDataList.get(forexDataList.size() - 1).getClose();
      double trend = statService.getTrend(pivot, newMean, oEntity.statEntity.sd);
      Assert.assertEquals(true, trend >= 0 && trend <= 1);
      String lastUpdatedTime = endDateStr;
      
      List<ForexDataNew> futureDataList = forexService.getForexDataSamples("TAIEX", period, lastUpdatedTime, numSamples);
      Assert.assertEquals(numSamples, futureDataList.size());
      
      double actualTrend = statService.getTrend(futureDataList, pivot);
      Assert.assertEquals(true, actualTrend >= 0 && actualTrend <= 1);
      
      if (trend >= 0.5 && actualTrend >= 0.5) {
          numRight++;
      } else if (trend < 0.5 && actualTrend < 0.5) {
          numRight++;
      } else {
          numWrong++;
      }
      System.out.println("numRight = " + numRight + ", numWrong = " + numWrong + ", time " + lastUpdatedTime);
      
      for (int i = 1; i <= 300; i++) {
         String nextUpdateTime = ForexUtils.getNextMinute(lastUpdatedTime, period);
         List<ForexDataNew> newDataList = forexService.getForexDataBetweenTime("TAIEX", period, lastUpdatedTime, nextUpdateTime);
         if (newDataList.size() == 0) {
             lastUpdatedTime = nextUpdateTime;
             continue;
         }
         lastUpdatedTime = nextUpdateTime;
         if (lastUpdatedTime.equalsIgnoreCase("2016-04-20 16:30")) {
            int x = 1;
            System.out.println(x);
         }
         Assert.assertEquals(1, newDataList.size());
         forexDataList.addAll(newDataList);
         oEntity = learningService.learningAndSave("TAIEX", period, forexDataList);
         
         newMean = statService.calculateMean(oEntity, code);
         
         pivot = newDataList.get(0).getClose();
         trend = statService.getTrend(pivot, newMean, oEntity.statEntity.sd);
         
         futureDataList = forexService.getForexDataSamples("TAIEX", period, lastUpdatedTime, numSamples);
         if (futureDataList.size() < numSamples) {
            break;
         }
         Assert.assertEquals(numSamples, futureDataList.size());
         actualTrend = statService.getTrend(futureDataList, pivot);
         Assert.assertEquals(true, actualTrend >= 0 && actualTrend <= 1);
         
         if (trend >= 0.5 && actualTrend >= 0.5) {
            numRight++;
         } else if (trend < 0.5 && actualTrend < 0.5) {
            numRight++;
         } else {
            numWrong++;
         }
         System.out.println("numRight = " + numRight + ", numWrong = " + numWrong + ", time " + lastUpdatedTime);
      }
      
      System.out.println(numRight);
      System.out.println(numWrong);
      System.out.println("Percent: " + (numRight * 100.0f / (numRight + numWrong)));
   }
   
   @Test
   public void testProbability() {
      double mean = 1.13174;
      double sd = 0.00075;
      NormalDistribution nd = new NormalDistribution(mean, sd);
      double pivot = 1.13157;
      double pr = nd.cumulativeProbability(pivot);
      Assert.assertEquals(true, pr >= 0);
   }
}
