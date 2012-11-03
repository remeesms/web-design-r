package org.zkoss.poi.ss.formula.functions;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.poi.ss.formula.AreaEvalHelper;
import org.zkoss.poi.ss.formula.eval.AreaEval;
import org.zkoss.poi.ss.formula.eval.BoolEval;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.usermodel.DateUtil;
import org.zkoss.util.Dates;

public class UtilFns
{
  public static final TimeZone TZ_GMT = TimeZone.getTimeZone("GMT");
  public static final Calendar CAL_GMT = Calendar.getInstance(TZ_GMT);
  private static final Date DATE1899_12_30;
  private static final Date DATE1900_3_1;
  private static final int PRECISION_SIZE = 15;

  public static Date stringToDate(ValueEval arg)
    throws EvaluationException
  {
    if (!(arg instanceof StringEval))
      throw new EvaluationException(ErrorEval.VALUE_INVALID);

    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    Date result = null;
    try {
      result = df.parse(((StringEval)arg).getStringValue());
    } catch (ParseException e) {
      throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
    return result;
  }

  public static boolean stringToBoolean(ValueEval arg) throws EvaluationException {
    if (!(arg instanceof BoolEval))
      throw new EvaluationException(ErrorEval.VALUE_INVALID);

    return ((BoolEval)arg).getBooleanValue();
  }

  public static int dsm(Date settle, Date maturi, int basis) throws EvaluationException {
    if (settle.compareTo(maturi) > 0)
      throw new EvaluationException(ErrorEval.NUM_ERROR);

    int dsm = 0;
    if ((basis == 0) || (basis == 4)) {
      SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
      Calendar calendar = df.getCalendar();
      calendar.setTime(settle);
      int settleY = calendar.get(1);
      int settleM = calendar.get(2);
      int settleD = (calendar.get(5) > 30) ? 30 : calendar.get(5);
      calendar.setTime(maturi);
      int maturiM = calendar.get(2);
      int maturiY = calendar.get(1);
      int maturiD = (calendar.get(5) > 30) ? 30 : calendar.get(5);
      dsm = (maturiY - settleY) * 360 + (maturiM - settleM) * 30 + maturiD - settleD;
    } else {
      dsm = (int)(DateUtil.getExcelDate(maturi) - DateUtil.getExcelDate(settle));
    }

    return dsm;
  }

  public static Long dateToDays(Date date)
  {
    long diff = Dates.subtract(date, TZ_GMT, 5, DATE1899_12_30);
    if (DATE1900_3_1.after(date)) diff -= 1L;
    return Long.valueOf(diff);
  }

  public static double basisToDouble(int basis, Date settle, Date maturi, int dsm) {
    double result = 0.0D;
    if ((basis == 0) || (basis == 2) || (basis == 4)) {
      result = 360.0D;
    } else if (basis == 1) {
      SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
      Calendar calendar = df.getCalendar();
      calendar.setTime(maturi);
      int mYear = calendar.get(1);
      int mMonth = calendar.get(2) + 1;
      int mDate = calendar.get(5);
      calendar.setTime(settle);
      int sYear = calendar.get(1);
      int sMonth = calendar.get(2) + 1;
      if (dsm > 366) {
        result = 365.5D;
      }
      else if (((new GregorianCalendar().isLeapYear(mYear)) && (((mMonth > 2) || ((mMonth == 2) && (mDate == 29))))) || ((new GregorianCalendar().isLeapYear(sYear)) && (sMonth < 3)))
      {
        result = 366.0D;
      }
      else result = 365.0D;

    }
    else if (basis == 3) {
      result = 365.0D;
    }
    return result; }

  public static String padZero(String num, int places) throws EvaluationException {
    if (places <= 0)
      throw new EvaluationException(ErrorEval.NUM_ERROR);

    StringBuffer result = new StringBuffer();
    for (int i = 0; i < places - num.length(); ++i)
      result.append("0");

    result.append(num);
    return result.toString();
  }

  public static String replaceiToi1(String complex, String suffix)
  {
    int i = complex.indexOf(suffix);
    while (i != -1) {
      if (!(Character.isDigit(complex.charAt(i - 1)))) {
        StringBuffer sf = new StringBuffer(complex);
        complex = sf.replace(i, i + 1, "1" + suffix).toString();
      }
      i = complex.indexOf(suffix, i + 1);
    }
    return complex;
  }

  public static Complex validateComplex(String complex)
    throws EvaluationException
  {
    Complex result = null;
    String s = "i";
    int i = complex.indexOf(s);
    if (i == -1) {
      s = "j";
      i = complex.indexOf(s);
      if (i == -1)
        s = "k";
    }

    ComplexFormat cf = new ComplexFormat(s);
    complex = replaceiToi1(complex, s);
    try {
      result = cf.parse(complex, s);
    } catch (ParseException e) {
      throw new EvaluationException(ErrorEval.NUM_ERROR);
    }
    return result;
  }

  public static Complex cToComplex(org.apache.commons.math.complex.Complex c, String suffix)
  {
    Complex result = new Complex(c.getReal(), c.getImaginary(), suffix);
    return result;
  }

  public static String format(Complex c)
  {
    NumberFormat nfr = NumberFormat.getInstance();
    nfr.setMaximumFractionDigits(14);
    NumberFormat nfi = NumberFormat.getInstance();
    nfi.setMaximumFractionDigits(14);
    ComplexFormat cf = new ComplexFormat(nfr, nfi);
    return cf.format(c);
  }

  public static int[] toIntArray(Object[] objs)
    throws EvaluationException
  {
    int[] in = new int[objs.length];
    for (int i = 0; i < objs.length; ++i) {
      if (objs[i] instanceof ErrorEval)
        throw new EvaluationException((ErrorEval)objs[i]);

      in[i] = ((Number)Classes.coerce(Integer.TYPE, objs[i])).intValue();
    }

    return in; }

  public static double[][] toDoubleMatrix(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
    if (arg instanceof AreaEval)
      return AreaEvalHelper.toDoubleMatrix(arg, srcRowIndex, srcColumnIndex);

    throw new EvaluationException(ErrorEval.VALUE_INVALID);
  }

  public static double toDouble15(double dbl, int roundingMode)
  {
    boolean neg = dbl < 0.0D;
    String dbs = "" + dbl;
    BigDecimal bdx = new BigDecimal(dbs);
    int sz = dbs.length();
    int dotj = -1;
    int expj = -1;
    int scale = 0;
    if (sz > 15) {
      for (int j = 0; j < sz; ++j) {
        char ch = dbs.charAt(j);
        if ('.' == ch) {
          dotj = j;
        } else if ('E' == ch) {
          expj = j;
          break;
        }
      }
      int useful = (expj >= 0) ? expj : sz;
      int expsz = (expj >= 0) ? Integer.parseInt(dbs.substring(expj + 1)) : 0;

      if (dotj >= 0)
        --useful;

      if (neg) {
        --useful;
        --dotj;
      }
      if (useful > 15) {
        if (dotj < 0) dotj = useful;
        scale = 15 - dotj - expsz;
        bdx = bdx.setScale(scale, roundingMode);
      }
    }
    return bdx.doubleValue();
  }

  public static List toList(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
    throws EvaluationException
  {
    List result = new LinkedList();

    for (int i = 0; i < args.length; ++i)
      toNumber(result, args[i], srcRowIndex, srcColumnIndex);

    return result;
  }

  private static void toNumber(List result, ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException
  {
    Double val;
    if (arg instanceof StringEval) {
      val = stringToDouble(TextFunctionHelper.evaluateStringArg(arg, srcRowIndex, srcColumnIndex), true);
      if (val == null)
        result.add(Objects.ZERO_DOUBLE);
      else
        result.add(val);
    }
    else if (arg instanceof NumberEval) {
      val = new Double(TextFunctionHelper.evaluateDoubleArg(arg, srcRowIndex, srcColumnIndex));
      result.add(val);
    } else if (arg instanceof BoolEval) {
      val = new Double(((BoolEval)arg).getNumberValue());
      result.add(val);
    } else if (arg instanceof AreaEval) {
      result.addAll(AreaEvalHelper.toDoubleList(arg, srcRowIndex, srcColumnIndex));
    }
  }

  public static Double stringToDouble(String str, boolean nullable)
    throws EvaluationException
  {
    try
    {
      return new Double(Double.parseDouble(str));
    } catch (NumberFormatException ex) {
      if (nullable)
        return null;

      throw new EvaluationException(ErrorEval.NUM_ERROR);
    }
  }

  public static double[] toDoubleArray(List ls)
    throws EvaluationException
  {
    LinkedList l = (LinkedList)ls;
    return toDoubleArray(l.toArray());
  }

  public static double[] toDoubleArray(Object[] objs)
    throws EvaluationException
  {
    double[] da = new double[objs.length];

    for (int i = 0; i < objs.length; ++i) {
      if (objs[i] instanceof ErrorEval)
        throw new EvaluationException((ErrorEval)objs[i]);

      da[i] = ((Number)Classes.coerce(Double.TYPE, objs[i])).doubleValue();
    }

    return da;
  }

  public static DescriptiveStatistics getStats(double[] d)
  {
    DescriptiveStatistics stats = DescriptiveStatistics.newInstance();
    for (int i = 0; i < d.length; ++i)
      stats.addValue(d[i]);

    return stats;
  }

  public static SimpleRegression getRegre(double[] xs, double[] ys)
    throws EvaluationException
  {
    if (xs.length != ys.length)
      throw new EvaluationException(ErrorEval.NA);

    SimpleRegression sr = new SimpleRegression();
    for (int i = 0; i < xs.length; ++i)
      sr.addData(xs[i], ys[i]);

    return sr;
  }

  public static Date daysToDate(int arg)
  {
    if (arg < 61) ++arg;
    return Dates.add(DATE1899_12_30, TZ_GMT, 5, arg);
  }

  static
  {
    CAL_GMT.clear();
    CAL_GMT.set(1899, 11, 30);
    DATE1899_12_30 = CAL_GMT.getTime();

    CAL_GMT.clear();
    CAL_GMT.set(1900, 2, 1);
    DATE1900_3_1 = CAL_GMT.getTime();
  }
}