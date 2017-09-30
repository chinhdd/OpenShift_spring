package org.jboss.tools.example.springmvc.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.jboss.tools.example.springmvc.domain.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebSocketController {

   @Autowired
   private SimpMessagingTemplate template;
   
   private TaskScheduler scheduler = new ConcurrentTaskScheduler();
   
   private List<Stock> stockPrices = new ArrayList<Stock>();
   
   private Random rand = new Random();
 
   private void updatePriceAndBroadcast() {
      for (Stock stock : stockPrices) {
         stock.setPrice(rand.nextDouble());
         stock.setTime(new Date());
      }
      template.convertAndSend("/topic/price", stockPrices);
   }
   
   @PostConstruct
   private void broadcastTimePeriodically() {
      scheduler.scheduleAtFixedRate(new Runnable() {
         
         @Override
         public void run() {
            updatePriceAndBroadcast();
         }
      }, 1000);
   }
   
   @MessageMapping("/addStock")
   public void addStock(Stock stock) {
      stockPrices.add(stock);
      updatePriceAndBroadcast();
   }
   
   @MessageMapping("/removeAllStocks")
   public void removeAllStocks() {
      stockPrices.clear();
      updatePriceAndBroadcast();
   }
   
   @RequestMapping(value = "/homeWS", method = RequestMethod.GET)
   public String homeWebSocket() {
      return "homeWebSocket";
   }
}
