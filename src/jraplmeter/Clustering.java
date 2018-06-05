/* ************************************************************************************************ 
 * Copyright 2016 SUNY Binghamton
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 * ***********************************************************************************************/

package jraplmeter;

import java.util.*;

import jraplmeter.Monitor.MonitoredField;

public class Clustering {

  public static class Cluster {
    public DataItem _mean;
    public double _count;

    public Cluster(DataItem item) { 
      _mean = new DataItem(item);
      _count = 0;
    }
  }


  // BIG-TODO: Can't share a monitor between multiple clusters as the monitor
  // state is manipulated by both. Items should only be added to a montior,
  // with clusters simply operating on the monitor data...

  public Monitor _monitor;
  public Cluster[] _clusters;

  public Clustering(Monitor monitor, int numClusters) { 
    _monitor = monitor;
    _clusters = new Cluster[numClusters];
  }

  public DataItem addItem(String className, Object item, MonitoredField[] fields) {
    int len = _monitor.numberItems();
    DataItem data = _monitor.addItem(className, item, fields);

    if (len < _clusters.length) {
      // Get an initial median
      _clusters[len] = new Cluster(data);
    } else {
      int minInd = -1;
      double minDist = Double.MAX_VALUE;
      for (int i = 0; i < _clusters.length; i++) {
        double distance = _monitor.distance(data, _clusters[i]._mean);
        if (Double.compare(minDist, distance) > 0) {
          minDist = distance;
          minInd = i;
        }
      }
      updateCluster(_clusters[minInd], data);
    }

    return data;
  }

  private void updateCluster(Cluster c, DataItem item) {
    c._count++;
    c._mean._fields[DataItem.JOULE_IND]._value += (1.0 / c._count) * _monitor.distanceNoAbs(item, c._mean);
    for (int i = 1; i < c._mean._fields.length; i++) {
      c._mean._fields[i]._value += (1.0 / c._count) * (item._fields[i]._value - c._mean._fields[i]._value);
    }
  }

  public Cluster attributeItem(DataItem item) {
    int minInd = -1;
    double minDist = Double.MAX_VALUE;
    for (int i = 0; i < _clusters.length; i++) {
      double distance = _monitor.distance(item, _clusters[i]._mean);
      if (Double.compare(minDist, distance) > 0) {
        minDist = distance;
        minInd = i;
      }
    }
    return _clusters[minInd];
  }

  public String toString() {
    String s = "";
    for (int i = 0; i < _clusters.length; i++) {
      String cs = "{";
      for (int j = 0; j < _clusters[i]._mean._fields.length; j++) {
        cs += String.format("%s:%.2f ", _clusters[i]._mean._fields[j]._name, _clusters[i]._mean._fields[j]._value);
      }
      s += cs + "} ";
    }
    return s;
  }

  public Cluster[] orderByJoule() {
    Cluster[] cs = Arrays.copyOf(_clusters, _clusters.length);
    Arrays.sort(cs, new Comparator<Cluster>() {
      @Override
      public int compare(Cluster c1, Cluster c2) {
        return Double.compare(c1._mean._fields[DataItem.JOULE_IND]._value, c2._mean._fields[DataItem.JOULE_IND]._value);
      }
    });
    return cs;
  }


}

