package org.jboss.tools.example.springmvc.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.utils.ForexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ForexDaoImpl implements ForexDao {

   @Autowired
   private EntityManager em;
   
   @Override
   public List<ForexDataNew> getData(String forexName, Short period,
         Date startDate, Date endDate) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<ForexDataNew> criteria = cb.createQuery(ForexDataNew.class);
      
      Root<ForexDataNew> forexData = criteria.from(ForexDataNew.class);
      
      Long startDateValue = ForexUtils.getDateValueFromDate(startDate);
      Long endDateValue = ForexUtils.getDateValueFromDate(endDate);
      
      criteria.select(forexData).where(cb.equal(forexData.get("forexName"), forexName),
            cb.equal(forexData.get("period"), period), 
            cb.between(forexData.<Long>get("dateValue"), startDateValue, endDateValue))
            .orderBy(cb.asc(forexData.get("dateValue")));
      return em.createQuery(criteria).getResultList();
   }

}
