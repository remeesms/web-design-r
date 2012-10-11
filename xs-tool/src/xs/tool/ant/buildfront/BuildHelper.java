package xs.tool.ant.buildfront;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * 
 * @author sushuang
 * @date 2012-02-27
 * 
 */
public class BuildHelper extends Task {

	public static String formatPath(Task that, String pathItem) {
		String baseDir = that.getProject().getBaseDir().getAbsolutePath();
		if (!pathItem.startsWith(baseDir)) {
			throw new BuildException("baseDirError: this.baseDir=" + baseDir
					+ " pathItem=" + pathItem);
		}
		String shor = pathItem.substring(baseDir.length());
		shor =  shor.replace("\\", "/");
		shor = shor.replaceFirst("^/+", "");
		return shor;
	}
	
	public static boolean isNotBlank(String str) {
		return str != null && str.trim().length() != 0;
	}
	
	public static String appendParamStr(String url, String paramStr) {
		if (url.indexOf('?') < 0) {
			url += '?' + paramStr;
		} else {
			url += '&' + paramStr;
		}
		return url;
	}
}