package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class WLJspc
  extends MatchingTask
{
  private File destinationDirectory;
  private File sourceDirectory;
  private String destinationPackage;
  private Path compileClasspath;
  private String pathToPackage = "";
  private List<String> filesToDo = new Vector();
  
  public void execute()
    throws BuildException
  {
    if (!destinationDirectory.isDirectory()) {
      throw new BuildException("destination directory %s is not valid", new Object[] {destinationDirectory.getPath() });
    }
    if (!sourceDirectory.isDirectory()) {
      throw new BuildException("src directory %s is not valid", new Object[] {sourceDirectory.getPath() });
    }
    if (destinationPackage == null) {
      throw new BuildException("package attribute must be present.", getLocation());
    }
    pathToPackage = destinationPackage.replace('.', File.separatorChar);
    
    DirectoryScanner ds = super.getDirectoryScanner(sourceDirectory);
    if (compileClasspath == null) {
      compileClasspath = new Path(getProject());
    }
    compileClasspath = compileClasspath.concatSystemClasspath();
    
    Java helperTask = new Java(this);
    helperTask.setFork(true);
    helperTask.setClassname("weblogic.jspc");
    helperTask.setTaskName(getTaskName());
    
    String[] args = new String[12];
    
    int j = 0;
    
    args[(j++)] = "-d";
    args[(j++)] = destinationDirectory.getAbsolutePath().trim();
    args[(j++)] = "-docroot";
    args[(j++)] = sourceDirectory.getAbsolutePath().trim();
    args[(j++)] = "-keepgenerated";
    
    args[(j++)] = "-compilerclass";
    args[(j++)] = "sun.tools.javac.Main";
    
    args[(j++)] = "-classpath";
    args[(j++)] = compileClasspath.toString();
    
    scanDir(ds.getIncludedFiles());
    log("Compiling " + filesToDo.size() + " JSP files");
    for (String filename : filesToDo)
    {
      File jspFile = new File(filename);
      args[j] = "-package";
      String parents = jspFile.getParent();
      if ((parents == null) || (parents.isEmpty()))
      {
        args[(j + 1)] = destinationPackage;
      }
      else
      {
        parents = replaceString(parents, File.separator, "_.");
        args[(j + 1)] = (destinationPackage + "._" + parents);
      }
      args[(j + 2)] = (sourceDirectory + File.separator + filename);
      helperTask.clearArgs();
      for (int x = 0; x < j + 3; x++) {
        helperTask.createArg().setValue(args[x]);
      }
      helperTask.setClasspath(compileClasspath);
      if (helperTask.executeJava() != 0) {
        log(filename + " failed to compile", 1);
      }
    }
  }
  
  public void setClasspath(Path classpath)
  {
    if (compileClasspath == null) {
      compileClasspath = classpath;
    } else {
      compileClasspath.append(classpath);
    }
  }
  
  public Path createClasspath()
  {
    if (compileClasspath == null) {
      compileClasspath = new Path(getProject());
    }
    return compileClasspath;
  }
  
  public void setSrc(File dirName)
  {
    sourceDirectory = dirName;
  }
  
  public void setDest(File dirName)
  {
    destinationDirectory = dirName;
  }
  
  public void setPackage(String packageName)
  {
    destinationPackage = packageName;
  }
  
  protected void scanDir(String[] files)
  {
    long now = Instant.now().toEpochMilli();
    for (String file : files)
    {
      File srcFile = new File(sourceDirectory, file);
      
      File jspFile = new File(file);
      String parents = jspFile.getParent();
      String pack;
      String pack;
      if ((parents == null) || (parents.isEmpty()))
      {
        pack = pathToPackage;
      }
      else
      {
        parents = replaceString(parents, File.separator, "_/");
        pack = pathToPackage + File.separator + "_" + parents;
      }
      String filePath = pack + File.separator + "_";
      
      int startingIndex = file.lastIndexOf(File.separator) != -1 ? file.lastIndexOf(File.separator) + 1 : 0;
      int endingIndex = file.indexOf(".jsp");
      if (endingIndex == -1)
      {
        log("Skipping " + file + ". Not a JSP", 3);
      }
      else
      {
        filePath = filePath + file.substring(startingIndex, endingIndex);
        filePath = filePath + ".class";
        File classFile = new File(destinationDirectory, filePath);
        if (srcFile.lastModified() > now) {
          log("Warning: file modified in the future: " + file, 1);
        }
        if (srcFile.lastModified() > classFile.lastModified())
        {
          filesToDo.add(file);
          log("Recompiling File " + file, 3);
        }
      }
    }
  }
  
  protected String replaceString(String inpString, String escapeChars, String replaceChars)
  {
    StringBuilder localString = new StringBuilder();
    StringTokenizer st = new StringTokenizer(inpString, escapeChars, true);
    int numTokens = st.countTokens();
    for (int i = 0; i < numTokens; i++)
    {
      String test = st.nextToken();
      test = test.equals(escapeChars) ? replaceChars : test;
      localString.append(test);
    }
    return localString.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.WLJspc
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */