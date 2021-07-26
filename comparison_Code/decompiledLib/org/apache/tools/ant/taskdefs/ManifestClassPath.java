package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class ManifestClassPath
  extends Task
{
  private String name;
  private File dir;
  private int maxParentLevels = 2;
  private Path path;
  
  public void execute()
  {
    if (name == null) {
      throw new BuildException("Missing 'property' attribute!");
    }
    if (dir == null) {
      throw new BuildException("Missing 'jarfile' attribute!");
    }
    if (getProject().getProperty(name) != null) {
      throw new BuildException("Property '%s' already set!", new Object[] { name });
    }
    if (path == null) {
      throw new BuildException("Missing nested <classpath>!");
    }
    StringBuilder tooLongSb = new StringBuilder();
    for (int i = 0; i < maxParentLevels + 1; i++) {
      tooLongSb.append("../");
    }
    String tooLongPrefix = tooLongSb.toString();
    
    FileUtils fileUtils = FileUtils.getFileUtils();
    dir = fileUtils.normalize(dir.getAbsolutePath());
    
    StringBuilder buffer = new StringBuilder();
    for (String element : path.list())
    {
      File pathEntry = new File(element);
      String fullPath = pathEntry.getAbsolutePath();
      pathEntry = fileUtils.normalize(fullPath);
      
      String relPath = null;
      String canonicalPath = null;
      try
      {
        if (dir.equals(pathEntry)) {
          relPath = ".";
        } else {
          relPath = FileUtils.getRelativePath(dir, pathEntry);
        }
        canonicalPath = pathEntry.getCanonicalPath();
        if (File.separatorChar != '/') {
          canonicalPath = canonicalPath.replace(File.separatorChar, '/');
        }
      }
      catch (Exception e)
      {
        throw new BuildException("error trying to get the relative path from " + dir + " to " + fullPath, e);
      }
      if ((relPath.equals(canonicalPath)) || 
        (relPath.startsWith(tooLongPrefix))) {
        throw new BuildException("No suitable relative path from %s to %s", new Object[] { dir, fullPath });
      }
      if ((pathEntry.isDirectory()) && (!relPath.endsWith("/"))) {
        relPath = relPath + '/';
      }
      relPath = Locator.encodeURI(relPath);
      
      buffer.append(relPath);
      buffer.append(' ');
    }
    getProject().setNewProperty(name, buffer.toString().trim());
  }
  
  public void setProperty(String name)
  {
    this.name = name;
  }
  
  public void setJarFile(File jarfile)
  {
    File parent = jarfile.getParentFile();
    if (!parent.isDirectory()) {
      throw new BuildException("Jar's directory not found: %s", new Object[] { parent });
    }
    dir = parent;
  }
  
  public void setMaxParentLevels(int levels)
  {
    if (levels < 0) {
      throw new BuildException("maxParentLevels must not be a negative number");
    }
    maxParentLevels = levels;
  }
  
  public void addClassPath(Path path)
  {
    this.path = path;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ManifestClassPath
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */