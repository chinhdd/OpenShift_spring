package org.jboss.tools.example.springmvc.service;

import java.util.List;

import org.jboss.tools.example.springmvc.entity.ForexDataNew;
import org.jboss.tools.example.springmvc.entity.OutputEntity;

public interface LearningService {

   public OutputEntity learningAndSave(String forexName, short period, List<ForexDataNew> dataList);
}
