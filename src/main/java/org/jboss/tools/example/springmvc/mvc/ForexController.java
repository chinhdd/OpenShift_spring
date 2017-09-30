package org.jboss.tools.example.springmvc.mvc;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.example.springmvc.domain.ForexData;
import org.jboss.tools.example.springmvc.domain.SComplexData;
import org.jboss.tools.example.springmvc.domain.SForexData;
import org.jboss.tools.example.springmvc.domain.SNNData;
import org.jboss.tools.example.springmvc.domain.SNNDataForTable;
import org.jboss.tools.example.springmvc.domain.SNNDataForTableEntity;
import org.jboss.tools.example.springmvc.domain.SNNInfo;
import org.jboss.tools.example.springmvc.domain.SWaveData;
import org.jboss.tools.example.springmvc.repo.ForexDataDao;
import org.jboss.tools.example.springmvc.service.NNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/forex")
public class ForexController {

   @Autowired
   private ForexDataDao forexDataDao;
   
   @Autowired
   private NNService nnService;
   
   @RequestMapping(value="/data/{name}", method=RequestMethod.GET, produces="application/json")
   @ResponseBody
   public List<SForexData> listAllForexDataByName(@PathVariable("name") String name) {
      List<ForexData> forexDataOri = forexDataDao.findAllOrderedByDate(name);
      List<SForexData> result = new ArrayList<SForexData>();
      for (ForexData data : forexDataOri) {
         SForexData sForex = new SForexData(data);
         result.add(sForex);
      }
      return result;
   }
   
   @RequestMapping(value="/dataWithWave/{name}", method=RequestMethod.GET, produces="application/json")
   @ResponseBody
   public SComplexData getDataWithWave(@PathVariable("name") String name) {
      return forexDataDao.getAllDataAndWave(name);
   }
   
   @RequestMapping(value="/wave/{name}", method=RequestMethod.GET, produces="application/json")
   @ResponseBody
   public List<SWaveData> getWaveData(@PathVariable("name") String name) {
      return forexDataDao.getWaveData(name);
   }
   
   @RequestMapping(value="/nn/{name}", method=RequestMethod.GET, produces="application/json")
   @ResponseBody
   public SNNDataForTableEntity getNNDataForTable(@PathVariable("name") String name) {
      List<SNNData> nnDataList = forexDataDao.getNNDataFromForex(name, 5, 10);
      List<SNNDataForTable> nnDataTableList = new ArrayList<SNNDataForTable>();
      for (int i = 0; i < nnDataList.size(); i++) {
         SNNData nnData = nnDataList.get(i);
         SNNDataForTable nnDataForTable = new SNNDataForTable(i + 1, nnData);
         nnDataTableList.add(nnDataForTable);
      }
      SNNInfo nnInfo = nnService.startLearningAndTest(name, 5, nnDataList);
      if (nnInfo.getActualOutput() != null) {
         for (int i = 0; i < 10; i++) {
            nnDataTableList.get(i).setActualOutput(nnInfo.getActualOutput().get(i));
         }
      }
      
      SNNDataForTableEntity nnEntity = new SNNDataForTableEntity(nnDataTableList, nnInfo);
      return nnEntity;
   }
}
