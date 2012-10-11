package xs.tool.ant.buildfront;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.ResourceUtils;

/**
 * replace content of file with random
 * 
 */
public class AddRandomParam extends Task {

	private String param = "pul__rrr";
	
	private String outputencoding = "UTF-8";
	
	private File srcfile; // input from build file (e.g. build.xml)
	
	private String targetstr; // input from build file (e.g. build.xml)
	
	private String delimeter = ";"; // input from build file (e.g. build.xml)
	
	public void execute() throws BuildException {

		validate();

		addRandomParam();
	}

	private void addRandomParam() {
		
		System.out.println("add random param.");
		
		FileResource fr = new FileResource(srcfile);

		String randomParamStr = generateRandomParamStr();

		String content = getContent();
		
		String[] targetstrArr = parseTargetstr();
		
		content = doAddRandomParam(content, targetstrArr, randomParamStr);
			
		save(content, fr);
	}

	private void validate() {
		if (!BuildHelper.isNotBlank(param)) {
			throw new IllegalArgumentException("illegal mode");
		}
		if (srcfile == null) {
			throw new IllegalArgumentException("illegal srcfile");
		}
		if (!BuildHelper.isNotBlank(targetstr)) {
			throw new IllegalArgumentException("illegal targetstr");
		}
		if (!BuildHelper.isNotBlank(delimeter)) {
			throw new IllegalArgumentException("illegal targetstr");
		}
	}
	
	private String[] parseTargetstr() {
		return targetstr.split(delimeter);
	}

	private String generateRandomParamStr() {
		return param + "=" + new Date().getTime();
	}
	
	private String getContent() {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			fis = new FileInputStream(srcfile);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String line = "";
			while((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (Exception ex) {
			throw new BuildException(ex);
		} finally {
			try { br.close(); } 
			catch (Exception ex) { throw new BuildException(ex); } 
		}
	}
	
	private String doAddRandomParam(String content, String[] targetstrArr, String randomParamStr) {
		for(String targetstr : targetstrArr) {
			System.out.println("replacing ... " + targetstr);
			String newStr = BuildHelper.appendParamStr(targetstr, randomParamStr);
			content = content.replace(targetstr, newStr);
		}
		return content;
	}
	
	private void save(String outputStr, FileResource fr) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(fr.getFile());
			fw.write(outputStr);
		} catch (Exception ex) {
			throw new BuildException(ex);
		} finally {
			try { fw.close(); }
			catch (Exception ex) { throw new BuildException(ex); }
		}
	}

	public String getOutputencoding() {
		return outputencoding;
	}

	public void setOutputencoding(String outputencoding) {
		this.outputencoding = outputencoding;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public File getSrcfile() {
		return srcfile;
	}

	public void setSrcfile(File srcfile) {
		this.srcfile = srcfile;
	}

	public String getTargetstr() {
		return targetstr;
	}

	public void setTargetstr(String targetstr) {
		this.targetstr = targetstr;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}
	
}
