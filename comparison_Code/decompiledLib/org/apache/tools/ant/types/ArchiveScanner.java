package org.apache.tools.ant.types;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public abstract class ArchiveScanner
  extends DirectoryScanner
{
  protected File srcFile;
  private Resource src;
  private Resource lastScannedResource;
  private Map<String, Resource> fileEntries = new TreeMap();
  private Map<String, Resource> dirEntries = new TreeMap();
  private Map<String, Resource> matchFileEntries = new TreeMap();
  private Map<String, Resource> matchDirEntries = new TreeMap();
  private String encoding;
  private boolean errorOnMissingArchive = true;
  
  public void setErrorOnMissingArchive(boolean errorOnMissingArchive)
  {
    this.errorOnMissingArchive = errorOnMissingArchive;
  }
  
  public void scan()
  {
    if ((src == null) || ((!src.isExists()) && (!errorOnMissingArchive))) {
      return;
    }
    super.scan();
  }
  
  public void setSrc(File srcFile)
  {
    setSrc(new FileResource(srcFile));
  }
  
  public void setSrc(Resource src)
  {
    this.src = src;
    FileProvider fp = (FileProvider)src.as(FileProvider.class);
    if (fp != null) {
      srcFile = fp.getFile();
    }
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public String[] getIncludedFiles()
  {
    if (src == null) {
      return super.getIncludedFiles();
    }
    scanme();
    return (String[])matchFileEntries.keySet().toArray(new String[matchFileEntries.size()]);
  }
  
  public int getIncludedFilesCount()
  {
    if (src == null) {
      return super.getIncludedFilesCount();
    }
    scanme();
    return matchFileEntries.size();
  }
  
  public String[] getIncludedDirectories()
  {
    if (src == null) {
      return super.getIncludedDirectories();
    }
    scanme();
    return (String[])matchDirEntries.keySet().toArray(new String[matchDirEntries.size()]);
  }
  
  public int getIncludedDirsCount()
  {
    if (src == null) {
      return super.getIncludedDirsCount();
    }
    scanme();
    return matchDirEntries.size();
  }
  
  Iterator<Resource> getResourceFiles(Project project)
  {
    if (src == null) {
      return new FileResourceIterator(project, getBasedir(), getIncludedFiles());
    }
    scanme();
    return matchFileEntries.values().iterator();
  }
  
  Iterator<Resource> getResourceDirectories(Project project)
  {
    if (src == null) {
      return new FileResourceIterator(project, getBasedir(), getIncludedDirectories());
    }
    scanme();
    return matchDirEntries.values().iterator();
  }
  
  public void init()
  {
    if (includes == null)
    {
      includes = new String[1];
      includes[0] = "**";
    }
    if (excludes == null) {
      excludes = new String[0];
    }
  }
  
  public boolean match(String path)
  {
    String vpath = path;
    if (!path.isEmpty())
    {
      vpath = path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
      if (vpath.charAt(0) == File.separatorChar) {
        vpath = vpath.substring(1);
      }
    }
    return (isIncluded(vpath)) && (!isExcluded(vpath));
  }
  
  public Resource getResource(String name)
  {
    if (src == null) {
      return super.getResource(name);
    }
    if (name.isEmpty()) {
      return new Resource("", true, Long.MAX_VALUE, true);
    }
    scanme();
    if (fileEntries.containsKey(name)) {
      return (Resource)fileEntries.get(name);
    }
    name = trimSeparator(name);
    if (dirEntries.containsKey(name)) {
      return (Resource)dirEntries.get(name);
    }
    return new Resource(name);
  }
  
  protected abstract void fillMapsFromArchive(Resource paramResource, String paramString, Map<String, Resource> paramMap1, Map<String, Resource> paramMap2, Map<String, Resource> paramMap3, Map<String, Resource> paramMap4);
  
  private void scanme()
  {
    if ((!src.isExists()) && (!errorOnMissingArchive)) {
      return;
    }
    Resource thisresource = new Resource(src.getName(), src.isExists(), src.getLastModified());
    if ((lastScannedResource != null) && 
      (lastScannedResource.getName().equals(thisresource.getName()))) {
      if (lastScannedResource.getLastModified() == thisresource.getLastModified()) {
        return;
      }
    }
    init();
    
    fileEntries.clear();
    dirEntries.clear();
    matchFileEntries.clear();
    matchDirEntries.clear();
    fillMapsFromArchive(src, encoding, fileEntries, matchFileEntries, dirEntries, matchDirEntries);
    
    lastScannedResource = thisresource;
  }
  
  protected static final String trimSeparator(String s)
  {
    return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.ArchiveScanner
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */