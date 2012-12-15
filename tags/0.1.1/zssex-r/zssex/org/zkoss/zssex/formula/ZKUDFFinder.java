package org.zkoss.zssex.formula;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.poi.ss.formula.functions.FreeRefFunction;
import org.zkoss.poi.ss.formula.udf.UDFFinder;

public class ZKUDFFinder
  implements UDFFinder
{
  private static Map<String, FreeRefFunction> FNS = new HashMap(3);
  public static final UDFFinder instance;

  public FreeRefFunction findFunction(String name)
  {
    if ((name != null) && (name.startsWith("_xlfn.")))
      name = name.substring(6);

    FreeRefFunction fn = (FreeRefFunction)FNS.get(name);
    return ((fn == null) ? new ELEvalFunction(name) : fn);
  }

  static
  {
    FNS.put("ELEVAL", ELEval.instance);

    instance = new ZKUDFFinder();
  }
}