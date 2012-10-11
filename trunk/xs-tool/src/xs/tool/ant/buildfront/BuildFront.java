package xs.tool.ant.buildfront;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.ResourceUtils;

/**
 * Create js and css file for ER debug
 * 
 * @author sushuang
 * @date 2011-11-05
 * 
 */
public class BuildFront extends Task {

	private String buildtype = "js";

	private String outputencoding = "UTF-8";

	private Resource dest = null;

	private Path filePath = null;
	
	public void execute() throws BuildException {

		System.out.println("buildType=" + buildtype);

		validate();
		
		String fileStr = createFileStr();
		
		save(fileStr);

	}

	private void save(String outputStr) {
		StringResource s = new StringResource(outputStr);
		try {
			ResourceUtils.copyResource(s, dest, null, null, true, false,
					"utf-8", outputencoding, getProject());
		} catch (Exception ex) {
			throw new BuildException("desc=" + dest, ex);
		}

	}

	private void validate() {
		if (filePath == null) {
			throw new BuildException("ERROR: must indicate path!");
		}
		if (dest == null) {
			throw new BuildException("ERROR: must indicate destfile!");
		}
	}

	private String createFileStr() {

		StringBuilder sb = new StringBuilder();
		
		createTitle(sb);
		
		String[] pathStrs = filePath.list();
		for (int i = 0; i < pathStrs.length; i++) {
			String pathItem = pathStrs[i];
			if (BuildHelper.isNotBlank(pathItem)) {

				pathItem = pathItem.trim();
				if (this.buildtype.equalsIgnoreCase("js")) {
					createJsLine(sb, BuildHelper.formatPath(this, pathItem));
				} else {
					createCssLine(sb, BuildHelper.formatPath(this, pathItem));
				}
			}
		}
		return sb.toString();
	}

	private void createTitle(StringBuilder sb) {
		sb.append("\n /* This file is generated by ant plug-in BuildFront.jar. */ \n \n");
	}
	
	private void createJsLine(StringBuilder sb, String pathItem) {

		sb.append("document.write( '<script src=\"");
		sb.append(pathItem);
		sb.append("\" type=\"text/javascript\"></script>' );  \n");
	}

	private void createCssLine(StringBuilder sb, String pathItem) {

		sb.append("@import url(../../");
		sb.append(pathItem);
		sb.append("); \n");
	}

	public void addPath(Path path) {
		getPath().add(path);
	}
	
	public void addFileSet(FileSet fileSet) {
		getPath().add(fileSet);
	}
	
	public void addFileList(FileList fileList) {
		getPath().add(fileList);
	}
	
	public Path getPath() {
		return filePath != null ? filePath : (filePath = new Path(getProject()));
	}

	public void setDestfile(File destinationFile) {
		setDest(new FileResource(destinationFile));
	}

	public void setDest(Resource dest) {
		this.dest = dest;
	}

	public String getBuildType() {
		return buildtype;
	}

	public void setBuildType(String buildType) {
		this.buildtype = buildType;
	}

	public String getOutputencoding() {
		return outputencoding;
	}

	public void setOutputencoding(String outputencoding) {
		this.outputencoding = outputencoding;
	}

}
