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

/* A monitor is associated with a single class */
public class Monitor {

  public static class MonitoredField {
    public String _name;
    public double _value;
    public MonitoredField(String name, double value) {
      _name = name;
      _value = value;
    }
  }

  public static final String MON_JOULES = "joules";
  public static final String MON_SECONDS = "seconds";

  public String _className;
  public List<DataItem> _items = new ArrayList<>();

  public String[] _fieldNames;
  public Map<String, Double> _runMean = new HashMap<>();
  //public Map<String, Double> _runDev = new HashMap<>();

  public Monitor(String className, String[] fieldNames) {
    _className = className;
    _fieldNames = new String[fieldNames.length+2];

    _runMean.put(MON_JOULES, 0.0);
    _runMean.put(MON_SECONDS, 0.0);
    _fieldNames[DataItem.JOULE_IND] = MON_JOULES;
    _fieldNames[DataItem.SECONDS_IND] = MON_SECONDS;

    for (int i = 0; i < fieldNames.length; i++) {
      _runMean.put(fieldNames[i], 0.0);
      _fieldNames[i+2] = fieldNames[i];
    }
    
  }

  public MonitoredField[] createMonitoredFields(Double[] values) {
    MonitoredField[] fields = new MonitoredField[values.length];
    for (int i = 0; i < fields.length; i++) {
      fields[i] = new MonitoredField(_fieldNames[i], values[i]);
    }
    return fields;
  }

  public DataItem addItem(String className, Object item, MonitoredField[] fields) {
    if (!className.equals(_className)) {
      throw new RuntimeException(String.format("Mis-matched internal classes %s %s\n", _className, className));
    }
    for (int i = 0; i < fields.length; i++) {
      if (!_runMean.containsKey(fields[i]._name)) {
        throw new RuntimeException(String.format("%s un-expected field %s\n", _className, fields[i]._name));
      }
      int n = _items.size();
      double mean = _runMean.get(fields[i]._name) + (1.0 / (double)n) * fields[i]._value;
      _runMean.put(fields[i]._name, mean);
    }
    DataItem data = new DataItem(className, item, fields);
    _items.add(data);
    return data;
  }

  public int numberItems() {
    return _items.size();
  }

  public double distance(DataItem x, DataItem y) {
    if (!x._className.equals(y._className) && !x._className.equals(_className)) {
      throw new RuntimeException(String.format("Mis-matched internal items %s %s %s\n", _className, x._className, y._className));
    }
    return Math.abs(x._fields[DataItem.JOULE_IND]._value - y._fields[DataItem.JOULE_IND]._value);
  }

  public double distanceNoAbs(DataItem x, DataItem y) {
    if (!x._className.equals(y._className) && !x._className.equals(_className)) {
      throw new RuntimeException(String.format("Mis-matched internal items %s %s %s\n", _className, x._className, y._className));
    }
    return x._fields[DataItem.JOULE_IND]._value - y._fields[DataItem.JOULE_IND]._value;
  }

  public static String parseClass(String str) {
    String[] toks = str.split(" ")[1].split("\\.");
    return toks[toks.length-1];
  } 

}
