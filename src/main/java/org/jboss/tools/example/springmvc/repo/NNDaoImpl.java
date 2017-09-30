package org.jboss.tools.example.springmvc.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.tools.example.springmvc.domain.NNWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class NNDaoImpl implements NNDao {
   
   @Autowired
   private EntityManager em;

   @Override
   public List<NNWeight> findAllWeightsByForex(String forexName, int period) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<NNWeight> criteria = cb.createQuery(NNWeight.class);
      Root<NNWeight> nnWeight = criteria.from(NNWeight.class);
      
      Predicate preName = cb.equal(nnWeight.get("forexName"), forexName);
      Predicate prePeriod = cb.equal(nnWeight.get("period"), period);
      criteria.select(nnWeight).where(preName, prePeriod).orderBy(cb.asc(nnWeight.get("layer")), 
            cb.asc(nnWeight.get("neuronTo")), cb.asc(nnWeight.get("neuronFrom")));
      return em.createQuery(criteria).getResultList();
   }

   @Override
   public boolean updateWeights(NNWeight weights, double value) {
      try {
         weights.setWeight(value);
         em.merge(weights);
      } catch (Exception e) {
         return false;
      }
      return true;
   }

   @Override
   public boolean updateWeightList(List<NNWeight> weightList) {
      try {
         for (NNWeight weight : weightList) {
            em.merge(weight);
         }
      } catch (Exception e) {
         return false;
      }
      return true;
   }

}
