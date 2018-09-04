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

import googoo.Monitor.MonitoredField;

public class DataItem { 
  public String _className;
  public Object _item;
  public MonitoredField[] _fields;

  public static final int JOULE_IND = 0;
  public static final int SECONDS_IND = 1;

  public DataItem(String className, Object item, MonitoredField[] fields) {
    _className = className;
    _item = item;
    _fields = fields;
  }

  public DataItem(DataItem item) {
    _className = item._className;
    _item = item._item;
    _fields = Arrays.copyOf(item._fields, item._fields.length);
  }

  public String toString() {
    String s = "";
    for (int i = 0; i < _fields.length; i++) {
      s += String.format("%s:%f ", _fields[i]._name, _fields[i]._value);
    }
    return s;
  }

}

