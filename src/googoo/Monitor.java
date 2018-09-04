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

package googoo;

import java.util.*;
import java.lang.Math;

import androidutil.*;

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
  public Map<String, Boolean> _skipField = new HashMap<>();

  public static final double PEARSONS_THRESHOLD = 0.5;

  public Monitor(String className, String[] fieldNames) {
    _className = className;
    _fieldNames = new String[fieldNames.length+2];

    _runMean.put(MON_JOULES, 0.0);
    _runMean.put(MON_SECONDS, 0.0);
    _fieldNames[DataItem.JOULE_IND] = MON_JOULES;
    _fieldNames[DataItem.SECONDS_IND] = MON_SECONDS;
    _skipField.put(MON_JOULES, false);
    _skipField.put(MON_SECONDS, false);

    for (int i = 0; i < fieldNames.length; i++) {
      _runMean.put(fieldNames[i], 0.0);
      _skipField.put(fieldNames[i], false);
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
      int n = _items.size()+1;
      double mean = _runMean.get(fields[i]._name);
      mean = mean + (1.0 / (double)n) * (fields[i]._value - mean);
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

  public double fieldDistance(DataItem x, DataItem y) {
    if (!x._className.equals(y._className) && !x._className.equals(_className)) {
      throw new RuntimeException(String.format("Mis-matched internal items %s %s %s\n", _className, x._className, y._className));
    }
    double dis = 0.0;
    for (int i = 2; i < x._fields.length; i++) {
      if (!_skipField.get(x._fields[i]._name)) {
        double diff = (x._fields[i]._value - y._fields[i]._value);
        dis += diff * diff;
      } 
    }
    return Math.sqrt(dis);
  }

  public static String parseClass(String str) {
    String[] toks = str.split(" ")[1].split("\\.");
    return toks[toks.length-1];
  } 

  // TODO: This needs to be re-written. Shouldn't be this terrible O(n) lookup
  private double fieldFromName(DataItem di, String fname) {
    for (int i = 0; i < di._fields.length; i++) {
      if (di._fields[i]._name.equals(fname)) {
        return di._fields[i]._value;
      }
    }
    throw new RuntimeException("looking for bad field " + fname);
  }

  private double pearsons(String fname) {
    double cov = 0.0;
    double xstd = 0.0;
    double ystd = 0.0;
    double xu = _runMean.get(MON_JOULES);
    double yu = _runMean.get(fname);
    for (int i = 0; i < _items.size(); i++) {
      DataItem di = _items.get(i);
      double xi = di._fields[DataItem.JOULE_IND]._value;
      double yi = fieldFromName(di, fname);
      cov += (xi - xu) * (yi - yu);
      xstd += (xi - xu) * (xi - xu);
      ystd += (yi - yu) * (yi - yu);
    }
    xstd = Math.sqrt(xstd);
    ystd = Math.sqrt(ystd);
    return cov / (xstd * ystd);
  }

  public void discardUnwantedFields() {
    for (int i = 2; i < _fieldNames.length; i++) {
      double r = pearsons(_fieldNames[i]);
      LogUtil.writeLogger(String.format("[SKIP] Pearsons for %s is %f\n", _fieldNames[i], r));
      if (Math.abs(r) < PEARSONS_THRESHOLD) {
        LogUtil.writeLogger(String.format("[SKIP] Discarding field %s with pearson %f\n", _fieldNames[i],r));
        _skipField.put(_fieldNames[i],true);
      }
    }
  }

}
