package org.jboss.tools.example.springmvc.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.tools.example.springmvc.domain.SComplexData;
import org.jboss.tools.example.springmvc.domain.SForexData;
import org.jboss.tools.example.springmvc.domain.SOutputPercent;
import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.domain.SWaveStatistics;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;
import org.jboss.tools.example.springmvc.service.ForexService;
import org.jboss.tools.example.springmvc.service.LearningService;
import org.jboss.tools.example.springmvc.service.StatisticsService;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ForexSocketController {

   private static final Log log = LogFactory.getLog(ForexSocketController.class);
   
   @Autowired
   private SimpMessagingTemplate template;
   
   @Autowired
   private ForexService forexService;
   
   @Autowired
   private LearningService learningService;
   
   @Autowired
   private StatisticsService statService;
   
   private TaskScheduler scheduler = new ConcurrentTaskScheduler();
   
   private List<ForexDataNew> forexDataList = new ArrayList<ForexDataNew>();
   private String lastUpdateTime;
   
   @PostConstruct
   private void broadcastPrice() {
      scheduler.scheduleAtFixedRate(new Runnable() {
         
         @Override
         public void run() {
            // updatePriceAndBroadcast();
         }
      }, 5000);
   }
   
   private void updatePriceAndBroadcast() {
      synchronized (forexDataList) {
         if (forexDataList.size() == 0) {
            //get data from Service
            String startDateStr = "2016-04-14 00:00";
            String endDateStr = "2016-04-19 21:00";
            forexDataList = forexService.getForexDataBetweenTime("EURUSD", (short)5, startDateStr, endDateStr);
            List<SForexData> forexShownList = new ArrayList<SForexData>();
            for (ForexDataNew forexNew : forexDataList) {
               forexShownList.add(new SForexData(forexNew));
            }
            List<SUnitData> waveList = forexService.getForexWaveIndex(forexDataList);
            OutputEntity oEntity = learningService.learningAndSave("EURUSD", (short)5, forexDataList);
            double newMean = statService.calculateMean(oEntity);
            lastUpdateTime = endDateStr;
            SWaveStatistics stat = new SWaveStatistics(null, null, null, null, String.format("%.5f", oEntity.statEntity.mean), 
                  String.format("%.5f", oEntity.statEntity.sd));
            SOutputPercent percentNewMean = new SOutputPercent(String.format("%.5f", newMean), null);
            SComplexData complexData = new SComplexData(String.format("%.5f", oEntity.output), forexShownList, waveList, stat, percentNewMean);
            template.convertAndSend("/topic/forex/EURUSD/5", complexData);
         } else {
            String nextUpdateTime = ForexUtils.getNextMinute(lastUpdateTime, 5);
            List<ForexDataNew> newDataList = forexService.getForexDataBetweenTime("EURUSD", (short)5, lastUpdateTime, nextUpdateTime);
            log.info(lastUpdateTime + ": " + newDataList.size());
            if (newDataList.size() > 0) {
               forexDataList.addAll(newDataList);
               List<SForexData> forexShownList = new ArrayList<SForexData>();
               for (ForexDataNew forexNew : forexDataList) {
                  forexShownList.add(new SForexData(forexNew));
               }
               List<SUnitData> waveList = forexService.getForexWaveIndex(forexDataList);
               OutputEntity oEntity = learningService.learningAndSave("EURUSD", (short)5, forexDataList);
               double newMean = statService.calculateMean(oEntity);
               SWaveStatistics stat = new SWaveStatistics(null, null, null, null, String.format("%.5f", oEntity.statEntity.mean), 
                     String.format("%.5f", oEntity.statEntity.sd));
               SOutputPercent percentNewMean = new SOutputPercent(String.format("%.5f", newMean), null);
               SComplexData complexData = new SComplexData(String.format("%.5f", oEntity.output), forexShownList, waveList, stat, percentNewMean);
               template.convertAndSend("/topic/forex/EURUSD/5", complexData);
            }
            lastUpdateTime = nextUpdateTime;
         }
      }
   }
   
   @MessageMapping("/listening")
   public void startListeningSocket() {
      //updatePriceAndBroadcast();
       //TODO
   }
   
   @RequestMapping(value = "/trade", method = RequestMethod.GET)
   public String homeForexSocket() {
      return "forexSocket";
   }
}
