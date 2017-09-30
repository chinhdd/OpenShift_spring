package org.jboss.tools.example.springmvc.repo;

import java.util.List;

import org.jboss.tools.example.springmvc.domain.NNWeight;

public interface NNDao {

   public List<NNWeight> findAllWeightsByForex(String forexName, int period);
   
   public boolean updateWeights(NNWeight weights, double value);
   
   public boolean updateWeightList(List<NNWeight> weightList);
}
