/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    ClassAssigner.java
 *    Copyright (C) 2002 Mark Hall
 *
 */

package weka.gui.beans;

import weka.core.Instances;
import weka.core.Instance;
import java.util.Vector;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.*;

/**
 * Describe class <code>ClassAssigner</code> here.
 *
 * @author <a href="mailto:mhall@cs.waikato.ac.nz">Mark Hall</a>
 * @version 1.0
 */
public class ClassAssigner extends JPanel
  implements Visible, DataSourceListener, TrainingSetListener, TestSetListener,
	     DataSource, TrainingSetProducer, TestSetProducer,
	     BeanCommon, EventConstraints, Serializable,
	     InstanceListener {
  
  private String m_classColumn = "last";

  private Object m_trainingProvider;
  private Object m_testProvider;
  private Object m_dataProvider;
  private Object m_instanceProvider;

  private Vector m_trainingListeners = new Vector();
  private Vector m_testListeners = new Vector();
  private Vector m_dataListeners = new Vector();
  private Vector m_instanceListeners = new Vector();

  protected transient weka.gui.Logger m_logger = null;

  protected BeanVisual m_visual = 
    new BeanVisual("ClassAssigner", 
		   BeanVisual.ICON_PATH+"ClassAssigner.gif",
		   BeanVisual.ICON_PATH+"ClassAssigner_animated.gif");

  public ClassAssigner() {
    setLayout(new BorderLayout());
    add(m_visual, BorderLayout.CENTER);    
  }

  public void setClassColumn(String col) {
    m_classColumn = col;
  }

  public String getClassColumn() {
    return m_classColumn;
  }

  public void acceptDataSet(DataSetEvent e) {
    Instances dataSet = e.getDataSet();
    assignClass(dataSet);
    notifyDataListeners(e);
  }

  public void acceptTrainingSet(TrainingSetEvent e) {
    Instances trainingSet = e.getTrainingSet();
    assignClass(trainingSet);
    notifyTrainingListeners(e);
  }

  public void acceptTestSet(TestSetEvent e) {
    Instances testSet = e.getTestSet();
    assignClass(testSet);
    notifyTestListeners(e);
  }

  /* 
  public void acceptInstance(InstanceEvent e) {
    assignClass(e.getInstance().dataset());
    notifyInstanceListeners(e);
    } */

  public void acceptInstance(InstanceEvent e) {
    if (e.getStatus() == InstanceEvent.FORMAT_AVAILABLE) {
      Instances dataSet = e.getInstance().dataset();
      //      System.err.println("Assigning class column...");
      assignClass(dataSet);
    }
    notifyInstanceListeners(e);
  }

  private void assignClass(Instances dataSet) {
    int classCol = -1;
    if (m_classColumn.toLowerCase().compareTo("last") == 0) {
      dataSet.setClassIndex(dataSet.numAttributes()-1);
    } else if (m_classColumn.toLowerCase().compareTo("first") == 0) {
      dataSet.setClassIndex(0);
    } else {
      classCol = Integer.parseInt(m_classColumn) - 1;
      if (classCol < 0 || classCol > dataSet.numAttributes()-1) {
	if (m_logger != null) {
	  m_logger.logMessage("Class column outside range of data "
			      +"(ClassAssigner)");
	}
      } else {
	dataSet.setClassIndex(classCol);
      }
    }
  }

  protected void notifyTestListeners(TestSetEvent tse) {
    Vector l;
    synchronized (this) {
      l = (Vector)m_testListeners.clone();
    }
    if (l.size() > 0) {
      for(int i = 0; i < l.size(); i++) {
	System.err.println("Notifying test listeners "
			   +"(ClassAssigner)");
	((TestSetListener)l.elementAt(i)).acceptTestSet(tse);
      }
    }
  }

  protected void notifyTrainingListeners(TrainingSetEvent tse) {
    Vector l;
    synchronized (this) {
      l = (Vector)m_trainingListeners.clone();
    }
    if (l.size() > 0) {
      for(int i = 0; i < l.size(); i++) {
	System.err.println("Notifying training listeners "
			   +"(ClassAssigner)");
	((TrainingSetListener)l.elementAt(i)).acceptTrainingSet(tse);
      }
    }
  }

  protected void notifyDataListeners(DataSetEvent tse) {
    Vector l;
    synchronized (this) {
      l = (Vector)m_dataListeners.clone();
    }
    if (l.size() > 0) {
      for(int i = 0; i < l.size(); i++) {
	System.err.println("Notifying data listeners "
			   +"(ClassAssigner)");
	((DataSourceListener)l.elementAt(i)).acceptDataSet(tse);
      }
    }
  }

  protected void notifyInstanceListeners(InstanceEvent tse) {
    Vector l;
    synchronized (this) {
      l = (Vector)m_instanceListeners.clone();
    }
    if (l.size() > 0) {
      for(int i = 0; i < l.size(); i++) {
	//	System.err.println("Notifying instance listeners "
	//			   +"(ClassAssigner)");
	((InstanceListener)l.elementAt(i)).acceptInstance(tse);
      }
    }
  }

  public synchronized void addInstanceListener(InstanceListener tsl) {
    m_instanceListeners.addElement(tsl);
  }

  public synchronized void removeInstanceListener(InstanceListener tsl) {
    m_instanceListeners.removeElement(tsl);
  }

  public synchronized void addDataSourceListener(DataSourceListener tsl) {
    m_dataListeners.addElement(tsl);
  }

  public synchronized void removeDataSourceListener(DataSourceListener tsl) {
    m_dataListeners.removeElement(tsl);
  }

  public synchronized void addTrainingSetListener(TrainingSetListener tsl) {
    m_trainingListeners.addElement(tsl);
  }

  public synchronized void removeTrainingSetListener(TrainingSetListener tsl) {
    m_trainingListeners.removeElement(tsl);
  }

  public synchronized void addTestSetListener(TestSetListener tsl) {
    m_testListeners.addElement(tsl);
  }

  public synchronized void removeTestSetListener(TestSetListener tsl) {
    m_testListeners.removeElement(tsl);
  }

  public void setVisual(BeanVisual newVisual) {
    m_visual = newVisual;
  }

  public BeanVisual getVisual() {
    return m_visual;
  }
  
  public void useDefaultVisual() {
    m_visual.loadIcons(BeanVisual.ICON_PATH+"ClassAssigner.gif",
		       BeanVisual.ICON_PATH+"ClassAssigner_animated.gif");
  }

  /**
   * Returns true if, at this time, 
   * the object will accept a connection according to the supplied
   * event name
   *
   * @param eventName the event
   * @return true if the object will accept a connection
   */
  public boolean connectionAllowed(String eventName) {
    if (eventName.compareTo("trainingSet") == 0 && 
	(m_trainingProvider != null || m_dataProvider != null ||
	 m_instanceProvider != null)) { 
      return false;
    }
    
    if (eventName.compareTo("testSet") == 0 && 
	m_testProvider != null) { 
      return false;
    }

     if (eventName.compareTo("instance") == 0 &&
	m_instanceProvider != null || m_trainingProvider != null ||
	 m_dataProvider != null) {
       return false;
     } 
    return true;
  }

  /**
   * Notify this object that it has been registered as a listener with
   * a source with respect to the supplied event name
   *
   * @param eventName the event
   * @param source the source with which this object has been registered as
   * a listener
   */
  public synchronized void connectionNotification(String eventName,
						  Object source) {
    if (connectionAllowed(eventName)) {
      if (eventName.compareTo("trainingSet") == 0) {
	m_trainingProvider = source;
      } else if (eventName.compareTo("testSet") == 0) {
	m_testProvider = source;
      } else if (eventName.compareTo("dataSet") == 0) {
	m_dataProvider = source;
      } else if (eventName.compareTo("instance") == 0) {
	m_instanceProvider = source;
      }
    }
  }

  /**
   * Notify this object that it has been deregistered as a listener with
   * a source with respect to the supplied event name
   *
   * @param eventName the event
   * @param source the source with which this object has been registered as
   * a listener
   */
  public synchronized void disconnectionNotification(String eventName,
						     Object source) {
    if (eventName.compareTo("trainingSet") == 0) {
      if (m_trainingProvider == source) {
	m_trainingProvider = null;
      }
    }
    if (eventName.compareTo("testSet") == 0) {
      if (m_testProvider == source) {
	m_testProvider = null;
      }
    }
    if (eventName.compareTo("dataSet") == 0) {
      if (m_dataProvider == source) {
	m_dataProvider = null;
      }
    }

    if (eventName.compareTo("instance") == 0) {
      if (m_instanceProvider == source) {
	m_instanceProvider = null;
      }
    }
  }
  
  public void setLog(weka.gui.Logger logger) {
    m_logger = logger;
  }

  public void stop() {
    // nothing to do
  }
  
  /**
   * Returns true, if at the current time, the named event could
   * be generated. Assumes that the supplied event name is
   * an event that could be generated by this bean
   *
   * @param eventName the name of the event in question
   * @return true if the named event could be generated at this point in
   * time
   */
  public boolean eventGeneratable(String eventName) {
    if (eventName.compareTo("trainingSet") == 0) { 
      if (m_trainingProvider == null) {
	return false;
      } else {
	if (m_trainingProvider instanceof EventConstraints) {
	  if (!((EventConstraints)m_trainingProvider).
	      eventGeneratable("trainingSet")) {
	    return false;
	  }
	}
      }
    }

    if (eventName.compareTo("dataSet") == 0) { 
      if (m_dataProvider == null) {
	return false;
      } else {
	if (m_dataProvider instanceof EventConstraints) {
	  if (!((EventConstraints)m_dataProvider).
	      eventGeneratable("dataSet")) {
	    return false;
	  }
	}
      }
    }

    if (eventName.compareTo("instance") == 0) { 
      if (m_instanceProvider == null) {
	return false;
      } else {
	if (m_instanceProvider instanceof EventConstraints) {
	  if (!((EventConstraints)m_instanceProvider).
	      eventGeneratable("instance")) {
	    return false;
	  }
	}
      }
    }

    if (eventName.compareTo("testSet") == 0) {
      if (m_testProvider == null) {
	return false;
      } else {
	if (m_testProvider instanceof EventConstraints) {
	  if (!((EventConstraints)m_testProvider).
	      eventGeneratable("testSet")) {
	    return false;
	  }
	}
      }
    }
    return true;
  }
}
