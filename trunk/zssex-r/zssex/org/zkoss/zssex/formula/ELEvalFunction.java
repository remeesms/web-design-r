package org.zkoss.zssex.formula;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.zkoss.poi.ss.formula.OperationEvaluationContext;
import org.zkoss.poi.ss.formula.eval.BoolEval;
import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.EvaluationException;
import org.zkoss.poi.ss.formula.eval.NotImplementedException;
import org.zkoss.poi.ss.formula.eval.NumberEval;
import org.zkoss.poi.ss.formula.eval.OperandResolver;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.FreeRefFunction;
import org.zkoss.poi.ss.usermodel.DateUtil;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelContext;
import org.zkoss.zss.model.impl.XelContextHolder;

public class ELEvalFunction
  implements org.zkoss.poi.ss.formula.functions.Function, FreeRefFunction
{
  private final String _functionName;

  public ELEvalFunction(String name)
  {
    this._functionName = name;
  }

  public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex)
  {
    XelContext ctx = XelContextHolder.getXelContext();
    if (ctx != null) {
      org.zkoss.xel.Function fn = ctx.getFunctionMapper().resolveFunction("zss", this._functionName);
      if (fn != null) {
        Class retType = fn.getReturnType();
        Class[] paramTypes = fn.getParameterTypes();
        try
        {
          if ((ValueEval.class.isAssignableFrom(retType)) && (paramTypes.length == 3) 
        		  && (org.zkoss.poi.ss.formula.eval.ValueEval[].class.isAssignableFrom(paramTypes[0])) && (Integer.TYPE.equals(paramTypes[1])) && (Integer.TYPE.equals(paramTypes[2])))
          {
            return ((ValueEval)fn.invoke(null, new Object[] { args, Integer.valueOf(srcRowIndex), Integer.valueOf(srcColumnIndex) }));
          }

          Object[] params = new Object[args.length];
          for (int j = 0; j < args.length; ++j) {
            Class paramType = paramTypes[j];
            ValueEval ev = OperandResolver.getSingleValue(args[j], srcRowIndex, srcColumnIndex);
            Object param = coerceToObject(paramType, ev);
            params[j] = param;
          }
          Object retValue = fn.invoke(null, params);
          return coerceToValueEval(retType, retValue);
        } catch (Exception e) {
          if (e instanceof EvaluationException)
            return ((EvaluationException)e).getErrorEval();

          if (e instanceof InvocationTargetException) {
            Throwable te = ((InvocationTargetException)e).getTargetException();
            if (te instanceof EvaluationException)
              return ((EvaluationException)te).getErrorEval();
          }
          else {
            return ErrorEval.VALUE_INVALID;
          }
        }
      } else {
        return ErrorEval.NAME_INVALID;
      }
    }
    throw new NotImplementedException(this._functionName);
  }

  private Object coerceToObject(Class<?> type, ValueEval value) throws EvaluationException {
    if (ValueEval.class.isAssignableFrom(type))
      return value;
    if (String.class.isAssignableFrom(type))
      return OperandResolver.coerceValueToString(value);
    if ((Integer.TYPE.equals(type)) || (Integer.class.isAssignableFrom(type)))
      return new Integer(OperandResolver.coerceValueToInt(value));
    if ((Short.TYPE.equals(type)) || (Short.class.isAssignableFrom(type)))
      return new Short((short)OperandResolver.coerceValueToInt(value));
    if ((Long.TYPE.equals(type)) || (Long.class.isAssignableFrom(type)))
      return new Long(OperandResolver.coerceValueToLong(value));
    if ((Byte.TYPE.equals(type)) || (Byte.class.isAssignableFrom(type)))
      return new Byte((byte)OperandResolver.coerceValueToInt(value));
    if ((Double.TYPE.equals(type)) || (Double.class.isAssignableFrom(type)))
      return new Double(OperandResolver.coerceValueToDouble(value));
    if ((Float.TYPE.equals(type)) || (Float.class.isAssignableFrom(type)))
      return new Float(OperandResolver.coerceValueToDouble(value));
    if (BigDecimal.class.isAssignableFrom(type))
      return new BigDecimal(OperandResolver.coerceValueToDouble(value));
    if (BigInteger.class.isAssignableFrom(type))
      return new BigInteger("" + OperandResolver.coerceValueToLong(value));
    if (Date.class.isAssignableFrom(type))
      return DateUtil.getJavaDate(OperandResolver.coerceValueToDouble(value));

    throw new EvaluationException(ErrorEval.VALUE_INVALID);
  }

  private ValueEval coerceToValueEval(Class<?> type, Object value) throws EvaluationException {
    if (String.class.isAssignableFrom(type))
      return new StringEval((String)value);
    if ((Number.class.isAssignableFrom(type)) || (Integer.TYPE.equals(type)) || (Double.TYPE.equals(type)) || (Float.TYPE.equals(type)) || (Long.TYPE.equals(type)) || (Short.TYPE.equals(type)) || (Byte.TYPE.equals(type)))
    {
      return new NumberEval(((Number)value).doubleValue()); }
    if (Boolean.class.isAssignableFrom(type))
      return BoolEval.valueOf(((Boolean)value).booleanValue());
    if (value instanceof ValueEval)
      return ((ValueEval)value);

    throw new EvaluationException(ErrorEval.VALUE_INVALID);
  }

  public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
    return evaluate(args, ec.getRowIndex(), ec.getColumnIndex());
  }
}