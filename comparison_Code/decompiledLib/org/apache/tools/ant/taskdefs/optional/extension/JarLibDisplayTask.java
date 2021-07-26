package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class JarLibDisplayTask
  extends Task
{
  private File libraryFile;
  private final List<FileSet> libraryFileSets = new Vector();
  
  public void setFile(File file)
  {
    libraryFile = file;
  }
  
  public void addFileset(FileSet fileSet)
  {
    libraryFileSets.add(fileSet);
  }
  
  public void execute()
    throws BuildException
  {
    validate();
    
    LibraryDisplayer displayer = new LibraryDisplayer();
    if (libraryFileSets.isEmpty()) {
      displayer.displayLibrary(libraryFile);
    } else {
      for (FileSet fileSet : libraryFileSets)
      {
        DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
        File basedir = scanner.getBasedir();
        for (String filename : scanner.getIncludedFiles()) {
          displayer.displayLibrary(new File(basedir, filename));
        }
      }
    }
  }
  
  private void validate()
    throws BuildException
  {
    if (null == libraryFile)
    {
      if (libraryFileSets.isEmpty()) {
        throw new BuildException("File attribute not specified.");
      }
    }
    else
    {
      if (!libraryFile.exists()) {
        throw new BuildException("File '%s' does not exist.", new Object[] { libraryFile });
      }
      if (!libraryFile.isFile()) {
        throw new BuildException("'%s' is not a file.", new Object[] { libraryFile });
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.JarLibDisplayTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */