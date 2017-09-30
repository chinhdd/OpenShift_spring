package org.jboss.tools.example.springmvc.dao;

import java.util.Date;
import java.util.List;

import org.jboss.tools.example.springmvc.entity.ForexDataNew;

public interface ForexDao {

   public List<ForexDataNew> getData(String forexName, Short period, Date startDate, Date endDate);
}
