package org.neuroph.myself;

import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventType;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;

public class InfoLearningEvent extends LearningEvent {
   
   private int mIterator;
   private double mTotalError;

   public InfoLearningEvent(IterativeLearning source, LearningEventType eventType) {
      super(source, eventType);
      mIterator = source.getCurrentIteration();
      if (source instanceof SupervisedLearning) {
         mTotalError = ((SupervisedLearning) source).getTotalNetworkError();
      }
   }

   public int getInterator() {
      return mIterator;
   }
   
   public double getTotalError() {
      return mTotalError;
   }
   
   @Override
   public String toString() {
      String str = "Event: " + eventType + ", Iterator: " + mIterator + ", Total Error: " + mTotalError;
      return str;
   }
}
