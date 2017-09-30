package org.jboss.tools.example.springmvc.service;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SUnitData;
import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;
import org.jboss.tools.example.springmvc.entity.StatEntity;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LearningServiceImpl implements LearningService {
   
   @Autowired
   private ForexService forexService;
   
   @Autowired
   private StatisticsService statService;
   
   @Autowired
   private NNService nnService;

   @Override
   public OutputEntity learningAndSave(String forexName, short period,
         List<ForexDataNew> dataList) {
      List<SUnitData> waveList = forexService.getForexWaveIndex(dataList);
      int beginIndex = 0;
      int endIndex = 0;
      StatEntity sEntity;
      List<Double> inputLearning;
      List<Double> standardInputValues;
      double outputForLearning;
      
      //now start learning
      List<SNNData> nnDataForLearningList = new ArrayList<SNNData>();
      for (int i = 0; i < 10; i++) {
         List<SUnitData> dataWave = forexService.getForexWaveIndexForLearning(dataList, waveList, i);
         // List<SUnitData> dataWave = forexService.getForexWaveIndexForLearning(dataList, waveList, i + 1);
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
      nnService.learningForex(forexName, period, nnDataForLearningList);
      
      //now calculate the next value
      List<SUnitData> curWave = forexService.getForexWaveIndexForLearning(dataList, waveList, -1);
      // List<SUnitData> curWave = forexService.getForexWaveIndexForLearning(dataList, waveList, 0);
      if (curWave.size() == 6) {
         beginIndex = curWave.get(5).getIndex();
         endIndex = curWave.get(0).getIndex();
         sEntity = statService.getStatValue(dataList, beginIndex, endIndex);
         //standardize input value for testing neural network
         inputLearning = new ArrayList<Double>();
         for (int j = 5; j >= 0; j--) {
            double value = (j % 2 == 0 ? dataList.get(curWave.get(j).getIndex()).getHigh()
                  : dataList.get(curWave.get(j).getIndex()).getLow());
            inputLearning.add(value);
         }
         standardInputValues = statService.getStandardizeFromZeroToOne(inputLearning, sEntity);
         SNNData curData = new SNNData(standardInputValues, null, null);
         nnService.predict(forexName, period, curData);
         double output = curData.getOutputValue();
         double adjustOutput = statService.adjustOutput(standardInputValues, output, 1);
         double curMaxValue = ForexUtils.getMaxValueInList(
               ForexUtils.createList(dataList.get(curWave.get(4).getIndex()).getHigh(),
                     dataList.get(curWave.get(2).getIndex()).getHigh(),
                     dataList.get(curWave.get(0).getIndex()).getHigh()));
         boolean isMaxExisted = statService.isMaxExisting(dataList, endIndex + 1, curMaxValue);
         if (isMaxExisted) {
            adjustOutput = statService.adjustOutputHigher(adjustOutput, sEntity, ForexUtils.getCode(forexName));
         }
         adjustOutput = statService.adjustOutputOverHigher(dataList, endIndex + 1, adjustOutput, sEntity, ForexUtils.getCode(forexName));
         adjustOutput = statService.checkModelHamerUp(dataList, curWave.get(1).getIndex(), adjustOutput);
         adjustOutput = statService.adjustOutputOverLower(dataList, endIndex + 1, adjustOutput, sEntity, ForexUtils.getCode(forexName));
         return new OutputEntity(adjustOutput, sEntity, 0);
      } else if (curWave.size() == 8) {
         beginIndex = curWave.get(7).getIndex();
         endIndex = curWave.get(2).getIndex();
         sEntity = statService.getStatValue(dataList, beginIndex, endIndex);
         
         inputLearning = new ArrayList<Double>();
         for (int j = 7; j >= 2; j--) {
             double value = (j % 2 == 0 ? dataList.get(curWave.get(j).getIndex()).getHigh()
                   : dataList.get(curWave.get(j).getIndex()).getLow());
             inputLearning.add(value);
         }
         standardInputValues = statService.getStandardizeFromZeroToOne(inputLearning, sEntity);
         SNNData curData = new SNNData(standardInputValues, null, null);
         nnService.predict(forexName, period, curData);
         beginIndex = curWave.get(2).getIndex();
         endIndex = curWave.get(0).getIndex();
         double realMean = statService.getStatValue(dataList, beginIndex, endIndex).mean;
         return new OutputEntity(curData.getOutputValue(), sEntity, realMean);
      }
      return null;
   }

}
