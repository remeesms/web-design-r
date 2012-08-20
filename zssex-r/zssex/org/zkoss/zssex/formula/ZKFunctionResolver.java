package org.zkoss.zssex.formula;

import org.zkoss.lang.Library;
import org.zkoss.poi.ss.formula.DependencyTracker;
import org.zkoss.poi.ss.formula.ZKDependencyTracker;
import org.zkoss.poi.ss.formula.udf.UDFFinder;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.TaglibMapper;
import org.zkoss.zss.formula.FunctionResolver;
import org.zkoss.zss.model.Book;

public class ZKFunctionResolver
  implements FunctionResolver
{
  private static final Log log = Log.lookup(ZKFunctionResolver.class);
  private static final String TAGLIB_KEY = "http://www.zkoss.org/zss/functions";
  private static FunctionMapper _mapper;
  private static UDFFinder _udffinder;
  private boolean _fail = false;

  public ZKFunctionResolver()
  {
    if (_mapper == null)
    {
      String str1 = Library.getProperty("http://www.zkoss.org/zss/functions");
      if (str1 != null)
      {
        TaglibMapper localTaglibMapper = new TaglibMapper();
        ClassLocator localClassLocator = new ClassLocator();
        String[] arrayOfString = str1.split(",");
        for (int i = 0; i < arrayOfString.length; ++i)
        {
          String str2 = arrayOfString[i];
          Taglib localTaglib = new Taglib("zss", "http://www.zkoss.org/zss/functions/" + str2.trim());
          try
          {
            localTaglibMapper.load(localTaglib, localClassLocator);
          }
          catch (XelException localXelException)
          {
            log.debug(localXelException);
          }
        }
        _mapper = localTaglibMapper;
      }
    }
    if (_udffinder == null)
      _udffinder = ZKUDFFinder.instance;
  }

  public UDFFinder getUDFFinder()
  {
    return ((this._fail) ? null : _udffinder);
  }

  public FunctionMapper getFunctionMapper()
  {
    return ((this._fail) ? null : _mapper);
  }

  public DependencyTracker getDependencyTracker(Book paramBook)
  {
    return new ZKDependencyTracker(paramBook);
  }
}