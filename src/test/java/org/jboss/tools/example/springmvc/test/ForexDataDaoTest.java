package org.jboss.tools.example.springmvc.test;

import java.util.ArrayList;
import java.util.List;


//import junit.framework.Assert;
import org.junit.Assert;
import org.jboss.tools.example.springmvc.domain.ForexData;
import org.jboss.tools.example.springmvc.domain.NNWeight;
import org.jboss.tools.example.springmvc.domain.SComplexData;
import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SNNDataForTable;
import org.jboss.tools.example.springmvc.domain.SNNDataForTableEntity;
import org.jboss.tools.example.springmvc.domain.SNNInfo;
import org.jboss.tools.example.springmvc.domain.SWaveData;
import org.jboss.tools.example.springmvc.repo.ForexDataDao;
import org.jboss.tools.example.springmvc.repo.NNDao;
import org.jboss.tools.example.springmvc.service.NNService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml",
      "classpath:/META-INF/spring/applicationContext.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ForexDataDaoTest {

   public static final double[][][] FINANCE_DATA = {
         { { 0.34, 0.71, 0.39, 0.60, 0.22, 0.57 }, { 0.0192 } },
         { { 0.23, 0.58, 0.34, 0.60, 0.40, 0.69 }, { 0.73 } } };
   public static final double[][][] FINANCE_DATA_NEW = { {
         { 0.25, 0.60, 0.30, 0.58, 0.35, 0.62 }, { 0.64 } } };

   @Autowired
   private ForexDataDao forexDataDao;
   
   @Autowired
   private NNDao nnDao;

   @Test
   public void testListAll() {
      List<ForexData> forexDataList = forexDataDao
            .findAllOrderedByDate("EURUSD");
      boolean moreThan1000 = forexDataList.size() > 1000;
      Assert.assertEquals(true, moreThan1000);
      forexDataList = forexDataDao.findAllOrderedByDate("Something");
      Assert.assertEquals(0, forexDataList.size());
      return;
   }

   @Test
   public void testWaveData() {
      List<SWaveData> waveList = forexDataDao.getWaveData("EURUSD");
      boolean moreThan2 = waveList.size() > 2;
      Assert.assertEquals(true, moreThan2);
      SWaveData wave = waveList.get(0);
      //System.out.println(wave.getDataList());
      //System.out.println(wave.getIndexList());
      // Assert.assertEquals(4, wave.getDataList().size());
   }

   @Test
   public void testDataWithWave() {
      SComplexData complexData = forexDataDao.getAllDataAndWave("EURUSD");
      boolean moreThan2 = complexData.getIndexList().size() > 2;
      Assert.assertEquals(true, moreThan2);
   }

   @Test
   public void testDataFinance() {
      
      List<NNWeight> weightList = nnDao.findAllWeightsByForex("EURUSD", 5);
      boolean moreThan5 = weightList.size() > 5;
      Assert.assertEquals(true, moreThan5);
      
      MultiLayerPerceptron nn = new MultiLayerPerceptron(6, 7, 1);
      
      //set all weights in neural network
      double[] weights = new double[weightList.size()];
      for (int i = 0; i < weightList.size(); i++) {
         weights[i] = weightList.get(i).getWeight();
      }
      nn.setWeights(weights);

      DataSet data = new DataSet(6, 1);
      for (int i = 0; i < FINANCE_DATA.length; i++) {
         DataSetRow row = new DataSetRow(FINANCE_DATA[i][0], FINANCE_DATA[i][1]);
         data.addRow(row);
      }

      nn.learn(data);

      data = new DataSet(6, 1);
      for (int i = 0; i < FINANCE_DATA_NEW.length; i++) {
         DataSetRow row = new DataSetRow(FINANCE_DATA_NEW[i][0],
               FINANCE_DATA_NEW[i][1]);
         data.addRow(row);
      }
      nn.learn(data);

      // now testing
      for (int i = 0; i < FINANCE_DATA.length; i++) {
         // DataSetRow row = new DataSetRow(LOGICAL_DATA_AND[i][0]);
         nn.setInput(FINANCE_DATA[i][0]);
         nn.calculate();
         double[] output = nn.getOutput();
         System.out.println(i + ": " + output[0]);
         boolean lessThanOne = output[0] < 1;
         Assert.assertEquals(true, lessThanOne);
      }
      for (int i = 0; i < FINANCE_DATA_NEW.length; i++) {
         // DataSetRow row = new DataSetRow(LOGICAL_DATA_AND[i][0]);
         nn.setInput(FINANCE_DATA_NEW[i][0]);
         nn.calculate();
         double[] output = nn.getOutput();
         System.out.println(i + ": " + output[0]);
         boolean lessThanOne = output[0] < 1;
         Assert.assertEquals(true, lessThanOne);
      }
   }
   
   @Test
   public void testNNWeight() {
      List<NNWeight> weightList = nnDao.findAllWeightsByForex("EURUSD", 5);
      boolean moreThan5 = weightList.size() > 5;
      Assert.assertEquals(true, moreThan5);
      //boolean mergeStatus = nnDao.updateWeights(weightList.get(2), 0.75f);
      //Assert.assertEquals(true, mergeStatus);
   }
   
   @Test
   public void testNNData() {
      List<SNNData> nnDataList = forexDataDao.getNNDataFromForex("EURUSD", 5, 10);
      Assert.assertEquals(true, nnDataList.size() == 10);
      
      List<SNNDataForTable> nnDataTableList = new ArrayList<SNNDataForTable>();
      for (int i = 0; i < nnDataList.size(); i++) {
         SNNData nnData = nnDataList.get(i);
         SNNDataForTable nnDataForTable = new SNNDataForTable(i + 1, nnData);
         nnDataTableList.add(nnDataForTable);
      }
      SNNDataForTableEntity nnEntity = new SNNDataForTableEntity(nnDataTableList, null);
      Assert.assertEquals(true, nnEntity.getData().size() == 10);
      
      List<NNWeight> weightList = nnDao.findAllWeightsByForex("EURUSD", 5);
      boolean moreThan5 = weightList.size() > 5;
      Assert.assertEquals(true, moreThan5);
      
      MultiLayerPerceptron nn = new MultiLayerPerceptron(6, 7, 1);
      
      //set all weights in neural network
      double[] weights = new double[weightList.size()];
      for (int i = 0; i < weightList.size(); i++) {
         weights[i] = weightList.get(i).getWeight();
      }
      nn.setWeights(weights);
      
      DataSet data = new DataSet(6, 1);
      for (int i = 0; i < nnDataList.size(); i++) {
         SNNData nnData = nnDataList.get(i);
         double[] input = new double[nnData.getInputList().size()];
         for (int j = 0; j < input.length; j++) {
            input[j] = nnData.getInputList().get(j);
         }
         double[] output = new double[1];
         output[0] = nnData.getOutputValue();
         DataSetRow row = new DataSetRow(input, output);
         data.addRow(row);
      }

      nn.learn(data);
      
      for (int i = 0; i < data.getRows().size(); i++) {
         DataSetRow row = data.getRowAt(i);
         nn.setInput(row.getInput());
         nn.calculate();
         double[] output = nn.getOutput();
         System.out.println("" + i + ": Desired: " + row.getDesiredOutput()[0] + ", Output: " + output[0]);
      }
   }
   
   @Autowired
   private NNService nnService;
   
   @Test
   public void testNNService() {
      List<SNNData> nnDataList = forexDataDao.getNNDataFromForex("EURUSD", 5, 10);
      Assert.assertEquals(true, nnDataList.size() == 10);
      boolean startLearning = nnService.startLearning("EURUSD", 5, nnDataList);
      Assert.assertEquals(true, startLearning);
   }
   
   @Test
   public void testNNServiceAndOutput() {
      List<SNNData> nnDataList = forexDataDao.getNNDataFromForex("EURUSD", 5, 10);
      Assert.assertEquals(10, nnDataList.size());
      long elapseTime = System.currentTimeMillis();
      SNNInfo nnInfo = nnService.startLearningAndTest("EURUSD", 5, nnDataList);
      elapseTime = System.currentTimeMillis() - elapseTime;
      Assert.assertNotNull(nnInfo.getActualOutput());
      Assert.assertEquals(10, nnInfo.getActualOutput().size());
      System.out.println(elapseTime);
   }
}
