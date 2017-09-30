package org.jboss.tools.example.springmvc.mvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.tools.example.springmvc.domain.SComplexData;
import org.jboss.tools.example.springmvc.domain.SForexData;
import org.jboss.tools.example.springmvc.domain.SOutputPercent;
import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.domain.SWaveStatistics;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;
import org.jboss.tools.example.springmvc.entity.RequestForexDataEntity;
import org.jboss.tools.example.springmvc.service.ForexService;
import org.jboss.tools.example.springmvc.service.LearningService;
import org.jboss.tools.example.springmvc.service.StatisticsService;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/testing")
public class TestingController {
   
   private static final Log log = LogFactory.getLog(TestingController.class);
   
   @Autowired
   private ForexService forexService;
   
   @Autowired
   private LearningService learningService;
   
   @Autowired
   private StatisticsService statService;

   @RequestMapping(method=RequestMethod.GET)
   public String getTestingPage() {
      return "forexTestPage";
   }
   
   @RequestMapping(value="/data", method = RequestMethod.GET)
   @ResponseBody
   public SComplexData getData(@RequestParam String name, @RequestParam String startDate,
         @RequestParam String endDate, @RequestParam String startTime, @RequestParam String endTime) {
      int code = ForexUtils.getCode(name);
      String startDateStr = ForexUtils.getStartDate(code);
      String endDateStr = ForexUtils.convertDateFromFrontEnd2BackEnd(startDate + " " + startTime);
      String finalDateStr = ForexUtils.convertDateFromFrontEnd2BackEnd(endDate + " " + endTime);
      log.info(startDateStr + ", " + endDateStr + ", " + finalDateStr);
      short period = ForexUtils.getPeriod(code);
      List<ForexDataNew> forexDataList = forexService.getForexDataBetweenTime(name, period, startDateStr, endDateStr);
      log.info(period + ", " + forexDataList.size());
      ArrayList<SForexData> sForexDataList = new ArrayList<SForexData>();
      for (ForexDataNew forexDataNew : forexDataList) {
         sForexDataList.add(new SForexData(forexDataNew));
      }
      List<SUnitData> waveList = forexService.getForexWaveIndex(forexDataList);
      String content = endDateStr + ";" + finalDateStr;
      return new SComplexData(content, sForexDataList, waveList, null, null);
   }
   
   @RequestMapping(value="/moreData", method = RequestMethod.GET)
   @ResponseBody
   public SComplexData getMoreData(@RequestParam String name, @RequestParam String lastDate, 
         @RequestParam String finalDate) {
      int code = ForexUtils.getCode(name);
      short period = ForexUtils.getPeriod(code);
      String startDateStr = ForexUtils.getStartDate(code);
      List<ForexDataNew> forexDataList = forexService.getForexDataBetweenTime(name, period, startDateStr, lastDate);
      OutputEntity oEntity = null;
      double newMean = 0;
      double pivot = 0;
      double trend = 0;
      List<ForexDataNew> futureDataList = null;
      double actualTrend = 0;
      List<ForexDataNew> newDataList = new ArrayList<ForexDataNew>();
      while (lastDate.equalsIgnoreCase(finalDate) == false) {
         String nextDate = ForexUtils.getNextMinute(lastDate, period);
         newDataList = forexService.getForexDataBetweenTime(name, 
               period, lastDate, nextDate);
         if (newDataList.size() == 0) {
            lastDate = nextDate;
            continue;
         }
         lastDate = nextDate;
         forexDataList.addAll(newDataList);
         oEntity = learningService.learningAndSave(name, period, forexDataList);
         newMean = statService.calculateMean(oEntity, code);
         pivot = newDataList.get(0).getClose();
         trend = statService.getTrend(pivot, newMean, oEntity.statEntity.sd);
         futureDataList = forexService.getForexDataSamples(name, period, lastDate, ForexUtils.FUTURE_SAMPLES);
         if (futureDataList.size() < ForexUtils.FUTURE_SAMPLES) {
            break;
         }
         actualTrend = statService.getTrend(futureDataList, pivot);
         break;
      }
      ArrayList<SForexData> sForexDataList = new ArrayList<SForexData>();
      for (ForexDataNew forexDataNew : forexDataList) {
         sForexDataList.add(new SForexData(forexDataNew));
      }
      List<SUnitData> waveList = forexService.getForexWaveIndex(forexDataList);
      String content = null;
      if (newDataList.size() > 0) {
         content = lastDate + ";" + finalDate + ";";
         if (trend >= 0.5) {
            content += "2";
         } else {
            content += "1";
         }
         content += ";";
         if (actualTrend >= 0.5) {
            content += "2";
         } else {
            content += "1";
         }
      }
      SWaveStatistics waveStat = new SWaveStatistics(null, null, null, 
            null, String.format("%2.5f", oEntity.statEntity.mean), 
            String.format("%2.6f", oEntity.statEntity.sd));
      SOutputPercent output = new SOutputPercent(String.format("%2.5f", newMean), null);
      return new SComplexData(content, sForexDataList, waveList, waveStat, output);
   }
}
