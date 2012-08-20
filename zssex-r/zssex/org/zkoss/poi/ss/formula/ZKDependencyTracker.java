package org.zkoss.poi.ss.formula;

import org.zkoss.poi.ss.formula.eval.ErrorEval;
import org.zkoss.poi.ss.formula.eval.NameEval;
import org.zkoss.poi.ss.formula.eval.NotImplementedException;
import org.zkoss.poi.ss.formula.eval.StringEval;
import org.zkoss.poi.ss.formula.eval.ValueEval;
import org.zkoss.poi.ss.formula.functions.FreeRefFunction;
import org.zkoss.zss.engine.impl.CellRefImpl;
import org.zkoss.zss.engine.impl.DependencyTrackerHelper;
import org.zkoss.zss.model.Book;

public class ZKDependencyTracker extends DefaultDependencyTracker
{
  public ZKDependencyTracker(Book book)
  {
    super(book);
  }

  public ValueEval postProcessValueEval(OperationEvaluationContext ec, ValueEval opResult, boolean eval)
  {
    ValueEval opResultX = super.postProcessValueEval(ec, opResult, eval);
    if ((eval) && (ErrorEval.NAME_INVALID.equals(opResultX)) && (opResult instanceof NameEval)) {
      String name = ((NameEval)opResult).getFunctionName();
      try {
        opResultX = UserDefinedFunction.instance.evaluate(new ValueEval[] { new NameEval("ELEVAL"), new StringEval("${" + name + "}") }, ec);

        if (!(opResultX instanceof ErrorEval)) {
          CellRefImpl srcRef = prepareSrcRef(ec);
          if (srcRef != null) {
            int j = name.indexOf(46);
            DependencyTrackerHelper.addDependency(srcRef, (j < 0) ? name : name.substring(0, j));
          }
        }
      }
      catch (NotImplementedException ex) {
      }
    }
    return opResultX;
  }
}