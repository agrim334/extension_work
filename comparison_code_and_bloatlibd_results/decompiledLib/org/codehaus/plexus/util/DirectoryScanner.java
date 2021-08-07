package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class DirectoryScanner
  extends AbstractScanner
{
  protected File basedir;
  protected Vector<String> filesIncluded;
  protected Vector<String> filesNotIncluded;
  protected Vector<String> filesExcluded;
  protected Vector<String> dirsIncluded;
  protected Vector<String> dirsNotIncluded;
  protected Vector<String> dirsExcluded;
  protected Vector<String> filesDeselected;
  protected Vector<String> dirsDeselected;
  protected boolean haveSlowResults = false;
  private boolean followSymlinks = true;
  protected boolean everythingIncluded = true;
  private final String[] tokenizedEmpty = MatchPattern.tokenizePathToString("", File.separator);
  
  public void setBasedir(String basedir)
  {
    setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
  }
  
  public void setBasedir(File basedir)
  {
    this.basedir = basedir;
  }
  
  public File getBasedir()
  {
    return basedir;
  }
  
  public void setFollowSymlinks(boolean followSymlinks)
  {
    this.followSymlinks = followSymlinks;
  }
  
  public boolean isEverythingIncluded()
  {
    return everythingIncluded;
  }
  
  public void scan()
    throws IllegalStateException
  {
    if (basedir == null) {
      throw new IllegalStateException("No basedir set");
    }
    if (!basedir.exists()) {
      throw new IllegalStateException("basedir " + basedir + " does not exist");
    }
    if (!basedir.isDirectory()) {
      throw new IllegalStateException("basedir " + basedir + " is not a directory");
    }
    setupDefaultFilters();
    setupMatchPatterns();
    
    filesIncluded = new Vector();
    filesNotIncluded = new Vector();
    filesExcluded = new Vector();
    filesDeselected = new Vector();
    dirsIncluded = new Vector();
    dirsNotIncluded = new Vector();
    dirsExcluded = new Vector();
    dirsDeselected = new Vector();
    if (isIncluded("", tokenizedEmpty))
    {
      if (!isExcluded("", tokenizedEmpty))
      {
        if (isSelected("", basedir)) {
          dirsIncluded.addElement("");
        } else {
          dirsDeselected.addElement("");
        }
      }
      else {
        dirsExcluded.addElement("");
      }
    }
    else {
      dirsNotIncluded.addElement("");
    }
    scandir(basedir, "", true);
  }
  
  protected void slowScan()
  {
    if (haveSlowResults) {
      return;
    }
    String[] excl = new String[dirsExcluded.size()];
    dirsExcluded.copyInto(excl);
    
    String[] notIncl = new String[dirsNotIncluded.size()];
    dirsNotIncluded.copyInto(notIncl);
    for (String anExcl : excl) {
      if (!couldHoldIncluded(anExcl)) {
        scandir(new File(basedir, anExcl), anExcl + File.separator, false);
      }
    }
    for (String aNotIncl : notIncl) {
      if (!couldHoldIncluded(aNotIncl)) {
        scandir(new File(basedir, aNotIncl), aNotIncl + File.separator, false);
      }
    }
    haveSlowResults = true;
  }
  
  protected void scandir(File dir, String vpath, boolean fast)
  {
    String[] newfiles = dir.list();
    if (newfiles == null) {
      newfiles = new String[0];
    }
    if (!followSymlinks)
    {
      ArrayList<String> noLinks = new ArrayList();
      for (String newfile : newfiles) {
        try
        {
          if (isParentSymbolicLink(dir, newfile))
          {
            String name = vpath + newfile;
            File file = new File(dir, newfile);
            if (file.isDirectory()) {
              dirsExcluded.addElement(name);
            } else {
              filesExcluded.addElement(name);
            }
          }
          else
          {
            noLinks.add(newfile);
          }
        }
        catch (IOException ioe)
        {
          String msg = "IOException caught while checking for links, couldn't get canonical path!";
          
          System.err.println(msg);
          noLinks.add(newfile);
        }
      }
      newfiles = (String[])noLinks.toArray(new String[noLinks.size()]);
    }
    if (filenameComparator != null) {
      Arrays.sort(newfiles, filenameComparator);
    }
    for (String newfile : newfiles)
    {
      String name = vpath + newfile;
      String[] tokenizedName = MatchPattern.tokenizePathToString(name, File.separator);
      File file = new File(dir, newfile);
      if (file.isDirectory())
      {
        if (isIncluded(name, tokenizedName))
        {
          if (!isExcluded(name, tokenizedName))
          {
            if (isSelected(name, file))
            {
              dirsIncluded.addElement(name);
              if (fast) {
                scandir(file, name + File.separator, fast);
              }
            }
            else
            {
              everythingIncluded = false;
              dirsDeselected.addElement(name);
              if ((fast) && (couldHoldIncluded(name))) {
                scandir(file, name + File.separator, fast);
              }
            }
          }
          else
          {
            everythingIncluded = false;
            dirsExcluded.addElement(name);
            if ((fast) && (couldHoldIncluded(name))) {
              scandir(file, name + File.separator, fast);
            }
          }
        }
        else
        {
          everythingIncluded = false;
          dirsNotIncluded.addElement(name);
          if ((fast) && (couldHoldIncluded(name))) {
            scandir(file, name + File.separator, fast);
          }
        }
        if (!fast) {
          scandir(file, name + File.separator, fast);
        }
      }
      else if (file.isFile())
      {
        if (isIncluded(name, tokenizedName))
        {
          if (!isExcluded(name, tokenizedName))
          {
            if (isSelected(name, file))
            {
              filesIncluded.addElement(name);
            }
            else
            {
              everythingIncluded = false;
              filesDeselected.addElement(name);
            }
          }
          else
          {
            everythingIncluded = false;
            filesExcluded.addElement(name);
          }
        }
        else
        {
          everythingIncluded = false;
          filesNotIncluded.addElement(name);
        }
      }
    }
  }
  
  protected boolean isSelected(String name, File file)
  {
    return true;
  }
  
  public String[] getIncludedFiles()
  {
    String[] files = new String[filesIncluded.size()];
    filesIncluded.copyInto(files);
    return files;
  }
  
  public String[] getNotIncludedFiles()
  {
    slowScan();
    String[] files = new String[filesNotIncluded.size()];
    filesNotIncluded.copyInto(files);
    return files;
  }
  
  public String[] getExcludedFiles()
  {
    slowScan();
    String[] files = new String[filesExcluded.size()];
    filesExcluded.copyInto(files);
    return files;
  }
  
  public String[] getDeselectedFiles()
  {
    slowScan();
    String[] files = new String[filesDeselected.size()];
    filesDeselected.copyInto(files);
    return files;
  }
  
  public String[] getIncludedDirectories()
  {
    String[] directories = new String[dirsIncluded.size()];
    dirsIncluded.copyInto(directories);
    return directories;
  }
  
  public String[] getNotIncludedDirectories()
  {
    slowScan();
    String[] directories = new String[dirsNotIncluded.size()];
    dirsNotIncluded.copyInto(directories);
    return directories;
  }
  
  public String[] getExcludedDirectories()
  {
    slowScan();
    String[] directories = new String[dirsExcluded.size()];
    dirsExcluded.copyInto(directories);
    return directories;
  }
  
  public String[] getDeselectedDirectories()
  {
    slowScan();
    String[] directories = new String[dirsDeselected.size()];
    dirsDeselected.copyInto(directories);
    return directories;
  }
  
  public boolean isSymbolicLink(File parent, String name)
    throws IOException
  {
    if (Java7Detector.isJava7()) {
      return NioFiles.isSymbolicLink(new File(parent, name));
    }
    File resolvedParent = new File(parent.getCanonicalPath());
    File toTest = new File(resolvedParent, name);
    return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
  }
  
  public boolean isParentSymbolicLink(File parent, String name)
    throws IOException
  {
    if (Java7Detector.isJava7()) {
      return NioFiles.isSymbolicLink(parent);
    }
    File resolvedParent = new File(parent.getCanonicalPath());
    File toTest = new File(resolvedParent, name);
    return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.DirectoryScanner
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */