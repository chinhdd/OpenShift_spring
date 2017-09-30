package org.jboss.tools.example.springmvc.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.tools.example.springmvc.domain.NNWeight;
import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SNNInfo;
import org.jboss.tools.example.springmvc.repo.NNDao;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.myself.InfoLearningEvent;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="nnService")
public class NNServiceImpl implements NNService {
   
   private static final Log log = LogFactory.getLog(NNServiceImpl.class);
    
   @Autowired
   private NNDao nnDao;

   @Override
   public boolean startLearning(String forexName, int period, List<SNNData> nnDataList) {
      int inputSize = 0;
      if (nnDataList.size() != 0) {
         inputSize = nnDataList.get(0).getInputList().size();
      }
      MultiLayerPerceptron nn = new MultiLayerPerceptron(inputSize, inputSize + 1, 1);
      DataSet data = new DataSet(inputSize, 1);
      for (int i = 0; i < nnDataList.size(); i++) {
         SNNData nnData = nnDataList.get(i);
         double[] input = new double[inputSize];
         for (int j = 0; j < inputSize; j++) {
            input[j] = nnData.getInputList().get(j);
         }
         double[] output = new double[1];
         output[0] = nnData.getOutputValue();
         DataSetRow row = new DataSetRow(input, output);
         data.addRow(row);
      }
      nn.learn(data);
      //save all to weight
      Double[] weights = nn.getWeights();
      List<NNWeight> weightList = nnDao.findAllWeightsByForex(forexName, period);
      if (weights.length == weightList.size()) {
         for (int i = 0; i < weights.length; i++) {
            weightList.get(i).setWeight(weights[i]);
         }
         //return nnDao.updateWeightList(weightList);
         return true;
      }
      return false;
   }

   @Override
   public SNNInfo startLearningAndTest(String forexName, int period,
         List<SNNData> nnDataList) {
      int inputSize = 0;
      if (nnDataList.size() != 0) {
         inputSize = nnDataList.get(0).getInputList().size();
      }
      MultiLayerPerceptron nn = new MultiLayerPerceptron(inputSize, inputSize + 1, 1);
      DataSet data = new DataSet(inputSize, 1);
      int sampleSize = nnDataList.size();
      for (int i = 0; i < sampleSize; i++) {
         SNNData nnData = nnDataList.get(i);
         double[] input = new double[inputSize];
         for (int j = 0; j < inputSize; j++) {
            input[j] = nnData.getInputList().get(j);
         }
         double[] output = new double[1];
         output[0] = nnData.getOutputValue();
         DataSetRow row = new DataSetRow(input, output);
         data.addRow(row);
      }
      final List<Float> networkError = new ArrayList<Float>();
      nn.getLearningRule().addListener(new LearningEventListener() {
         
         @Override
         public void handleLearningEvent(LearningEvent event) {
            //System.out.println("learning listener: event = " + event);
            if (event instanceof InfoLearningEvent) {
               networkError.add((float)((InfoLearningEvent)event).getTotalError());
            }
         }
      });
      nn.learn(data);
      //save all to weight
      Double[] weights = nn.getWeights();
      List<NNWeight> weightList = nnDao.findAllWeightsByForex(forexName, period);
      if (weights.length == weightList.size()) {
         for (int i = 0; i < weights.length; i++) {
            weightList.get(i).setWeight(weights[i]);
         }
         //if (nnDao.updateWeightList(weightList)) {
            List<String> testResult = new ArrayList<String>();
            for (int i = 0; i < sampleSize; i++) {
               DataSetRow row = data.getRowAt(i);
               nn.setInput(row.getInput());
               nn.calculate();
               testResult.add(String.format("%.5f", nn.getOutput()[0]));
            }
            return new SNNInfo(testResult, networkError);
         //}
      }
      return new SNNInfo(null, networkError);
   }

   @Override
   public boolean learningForex(String forexName, int period,
         List<SNNData> dataList) {
      List<NNWeight> weightList = nnDao.findAllWeightsByForex(forexName, period);
      if (weightList.size() == 0 || dataList.size() == 0) {
         return false;
      }
      int inputSize = dataList.get(0).getInputList().size();
      MultiLayerPerceptron nn = new MultiLayerPerceptron(inputSize, 2, 1);//inputSize and one output
      Double[] weights = nn.getWeights();
//      double[] weights = new double[weightList.size()];
//      for (int i = 0; i < weightList.size(); i++) {
//         weights[i] = weightList.get(i).getWeight();
//      }
//      nn.setWeights(weights);
      //for showing log
      StringBuilder str = new StringBuilder();
      str.append("[").append(weights[0]);
      for (int i = 1; i < weights.length; i++) {
          str.append(",").append(weights[i]);
      }
      str.append("]");
      log.info(str.toString());
      BackPropagation bpLearning = nn.getLearningRule();
      bpLearning.setMaxIterations(10000);
      DataSet dataSet = new DataSet(inputSize, 1);
      for (SNNData aData : dataList) {
         double[] inputData = new double[aData.getInputList().size()];
         for (int i = 0; i < inputData.length; i++) {
            inputData[i] = aData.getInputList().get(i);
         }
         double[] outputData = new double[1];
         outputData[0] = aData.getOutputValue();
         DataSetRow dataRow = new DataSetRow(inputData, outputData);
         dataSet.addRow(dataRow);
      }
      nn.learn(dataSet);
      Double[] weightsAfterLearning = nn.getWeights();
      if (weightsAfterLearning.length != weights.length) {
         return false;
      }
      for (int i = 0; i < weightsAfterLearning.length; i++) {
         weightList.get(i).setWeight(weightsAfterLearning[i]);
      }
      if (nnDao.updateWeightList(weightList) == false) {
         return false;
      }
      //for testing
      List<String> testResult = new ArrayList<String>();
      for (int i = 0; i < dataList.size(); i++) {
         DataSetRow row = dataSet.getRowAt(i);
         nn.setInput(row.getInput());
         nn.calculate();
         testResult.add(String.format("%.5f", nn.getOutput()[0]));
      }
      return true;
   }

   @Override
   public boolean learn(String forexName, int period, SNNData data) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean predict(String forexName, int period, SNNData curData) {
      List<NNWeight> weightList = nnDao.findAllWeightsByForex(forexName, period);
      if (weightList.size() == 0) {
         return false;
      }
      int inputSize = curData.getInputList().size();
      MultiLayerPerceptron nn = new MultiLayerPerceptron(inputSize, 2, 1);//inputSize and one output
      int weightSize = nn.getWeights().length;
      double[] weights = new double[weightSize];
      for (int i = 0; i < weightSize; i++) {
         weights[i] = weightList.get(i).getWeight();
      }
      nn.setWeights(weights);
      
      double[] inputs = new double[inputSize];
      for (int i = 0; i < inputSize; i++) {
         inputs[i] = curData.getInputList().get(i);
      }
      nn.setInput(inputs);
      nn.calculate();
      curData.setOutputValue(nn.getOutput()[0]);
      return true;
   }

}
