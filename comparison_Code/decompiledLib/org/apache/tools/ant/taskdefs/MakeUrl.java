package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class MakeUrl
  extends Task
{
  public static final String ERROR_MISSING_FILE = "A source file is missing: ";
  public static final String ERROR_NO_PROPERTY = "No property defined";
  public static final String ERROR_NO_FILES = "No files defined";
  private String property;
  private File file;
  private String separator = " ";
  private List<FileSet> filesets = new LinkedList();
  private List<Path> paths = new LinkedList();
  private boolean validate = true;
  
  public void setProperty(String property)
  {
    this.property = property;
  }
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public void addFileSet(FileSet fileset)
  {
    filesets.add(fileset);
  }
  
  public void setSeparator(String separator)
  {
    this.separator = separator;
  }
  
  public void setValidate(boolean validate)
  {
    this.validate = validate;
  }
  
  public void addPath(Path path)
  {
    paths.add(path);
  }
  
  private String filesetsToURL()
  {
    if (filesets.isEmpty()) {
      return "";
    }
    int count = 0;
    StringBuilder urls = new StringBuilder();
    for (FileSet fs : filesets)
    {
      DirectoryScanner scanner = fs.getDirectoryScanner(getProject());
      for (String file : scanner.getIncludedFiles())
      {
        File f = new File(scanner.getBasedir(), file);
        validateFile(f);
        String asUrl = toURL(f);
        urls.append(asUrl);
        log(asUrl, 4);
        urls.append(separator);
        count++;
      }
    }
    return stripTrailingSeparator(urls, count);
  }
  
  private String stripTrailingSeparator(StringBuilder urls, int count)
  {
    if (count > 0)
    {
      urls.delete(urls.length() - separator.length(), urls.length());
      return new String(urls);
    }
    return "";
  }
  
  private String pathsToURL()
  {
    if (paths.isEmpty()) {
      return "";
    }
    int count = 0;
    StringBuilder urls = new StringBuilder();
    for (Path path : paths) {
      for (String element : path.list())
      {
        File f = new File(element);
        validateFile(f);
        String asUrl = toURL(f);
        urls.append(asUrl);
        log(asUrl, 4);
        urls.append(separator);
        count++;
      }
    }
    return stripTrailingSeparator(urls, count);
  }
  
  private void validateFile(File fileToCheck)
  {
    if ((validate) && (!fileToCheck.exists())) {
      throw new BuildException("A source file is missing: " + fileToCheck);
    }
  }
  
  public void execute()
    throws BuildException
  {
    validate();
    if (getProject().getProperty(property) != null) {
      return;
    }
    String filesetURL = filesetsToURL();
    String url;
    String url;
    if (file == null)
    {
      url = filesetURL;
    }
    else
    {
      validateFile(file);
      url = toURL(file);
      if (!filesetURL.isEmpty()) {
        url = url + separator + filesetURL;
      }
    }
    String pathURL = pathsToURL();
    if (!pathURL.isEmpty()) {
      if (url.isEmpty()) {
        url = pathURL;
      } else {
        url = url + separator + pathURL;
      }
    }
    log("Setting " + property + " to URL " + url, 3);
    getProject().setNewProperty(property, url);
  }
  
  private void validate()
  {
    if (property == null) {
      throw new BuildException("No property defined");
    }
    if ((file == null) && (filesets.isEmpty()) && (paths.isEmpty())) {
      throw new BuildException("No files defined");
    }
  }
  
  private String toURL(File fileToConvert)
  {
    return FileUtils.getFileUtils().toURI(fileToConvert.getAbsolutePath());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MakeUrl
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */