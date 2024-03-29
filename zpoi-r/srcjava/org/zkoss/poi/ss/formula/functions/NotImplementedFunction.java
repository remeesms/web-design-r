/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.zkoss.poi.ss.formula.functions;

import org.zkoss.poi.ss.formula.eval.NotImplementedException;
import org.zkoss.poi.ss.formula.eval.ValueEval;

/**
 *
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 * This is the default implementation of a Function class.
 * The default behaviour is to raise a POI internal error
 * ({@link NotImplementedException}). This error should alert
 * the user that the formula contained a function that is not
 * yet implemented.
 */
public final class NotImplementedFunction implements Function {
	private final String _functionName;
	//20120320, henrichen@zkoss.org: ZSS-103
	private Function _func;
	private static Class _funcClass;
	static {
		try {
			_funcClass = Thread.currentThread().getClass().forName("org.zkoss.zssex.formula.ELEvalFunction");
		} catch (ClassNotFoundException e) {
			//ignore
		}		
	}
	protected NotImplementedFunction() {
		_functionName = getClass().getName();
	}
	public NotImplementedFunction(String name) {
		_functionName = name;
		//20120320, henrichen@zkoss.org: ZSS-103
		if (_funcClass != null) {
			try {
				_func = (Function) _funcClass.getConstructor(String.class).newInstance(name);
			} catch(Exception ex) {
				//ignore
			}
		}
	}

	public ValueEval evaluate(ValueEval[] operands, int srcRow, int srcCol) {
		//20120320, henrichen@zkoss.org: ZSS-103
		if (_func != null)
			return _func.evaluate(operands, srcRow, srcCol);
		throw new NotImplementedException(_functionName);
	}
	public String getFunctionName() {
		return _functionName;
	}
}
