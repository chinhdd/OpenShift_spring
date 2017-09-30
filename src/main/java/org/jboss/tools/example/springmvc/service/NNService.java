package org.jboss.tools.example.springmvc.service;

import java.util.List;

import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SNNInfo;

public interface NNService {

   public boolean startLearning(String forexName, int period, List<SNNData> data);
   
   public SNNInfo startLearningAndTest(String forexName, int period, List<SNNData> data);
   
   public boolean learningForex(String forexName, int period, List<SNNData> dataList);
   
   public boolean learn(String forexName, int period, SNNData data);
   
   public boolean predict(String forexName, int period, SNNData curData);
}
