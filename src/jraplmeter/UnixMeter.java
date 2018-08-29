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

import jraplmeter.MeterReading.MeterVector;

import jrapl.EnergyCheckUtils;

import com.google.common.base.Stopwatch;

public class UnixMeter {
  public static final int DRAM = 0;
  public static final int CPU = 1;
  public static final int PACKAGE = 2;

  private static UnixMeter _meter;
  static {
    _meter = new UnixMeter();
  }

  public static UnixMeter sharedMeter() {
    return _meter;
  }

  private class UnixReading implements MeterReading {
    public double[] _jrapl;
    public int      _sockets; 
    public long     _time;

    public UnixReading(double[] jrapl, int sockets) {
      _jrapl    = jrapl;
      _sockets  = sockets;
      _time     = System.nanoTime();
    }

    public UnixReading(double[] jrapl, int sockets, long time) {
      _jrapl    = jrapl;
      _sockets  = sockets;
      _time     = time;
    }
  }

  public UnixMeter() { } 

  public void close() {
    EnergyCheckUtils.ProfileDealloc();
  }

  public MeterReading read() {
    double[] readings = EnergyCheckUtils.getEnergyStats();
    return new UnixReading(readings, EnergyCheckUtils.socketNum);
  }

  public MeterReading diff(MeterReading start) {
    UnixReading r1 = (UnixReading) start;
    UnixReading r2 = (UnixReading) read();
    double[] readings = new double[r1._jrapl.length];
    for (int i = 0; i < r1._jrapl.length; i++) {
      readings[i] = r2._jrapl[i] - r1._jrapl[i];
      if (readings[i] < 0) {
        readings[i] += EnergyCheckUtils.wraparoundValue;
      }
    } 

    /*
    LogUtil.writeLogger("[SKIP] Raw Diff: ");
    for (int i = 0; i < readings.length; i++) {
      LogUtil.writeLogger(String.format("%.2f ", readings[i]));
    }
    LogUtil.writeLogger("\n");
    */

    return new UnixReading(readings, r1._sockets, r2._time - r1._time);
  }

  public MeterVector asJoules(MeterReading mr) {
    MeterVector vector = new MeterVector();

    UnixReading ur = (UnixReading) mr;
    double packageTotal = 0.0;
    for (int i = 0; i < ur._sockets; i++) {
      packageTotal += ur._jrapl[(i*3)+PACKAGE];
    }
    vector.put("package", packageTotal);

    double cpuTotal = 0.0;
    for (int i = 0; i < ur._sockets; i++) {
      cpuTotal += ur._jrapl[(i*3)+CPU];
    }
    vector.put("cpu", cpuTotal);

    double dramTotal = 0.0;
    for (int i = 0; i < ur._sockets; i++) {
      dramTotal += ur._jrapl[(i*3)+DRAM];
    }
    vector.put("dram", dramTotal);

    double uncoreTotal = packageTotal - cpuTotal;
    vector.put("uncore", uncoreTotal);

    vector.put("total", packageTotal + dramTotal);

    vector.put("seconds", (double) ur._time / 1000000000.0);


    return vector;
  }

  public MeterVector asWatts(MeterReading mr) {
    UnixReading ur = (UnixReading) mr;
    double seconds = (double) ur._time / 1000000000.0;

    MeterVector vector = asJoules(ur); 

    vector.put("total", vector.get("total") / seconds);
    vector.put("package", vector.get("package") / seconds);
    vector.put("cpu", vector.get("cpu") / seconds);
    vector.put("dram", vector.get("dram") / seconds);
    vector.put("uncore", vector.get("uncore") / seconds);
    vector.put("seconds", seconds);

    return vector;
  }

}


