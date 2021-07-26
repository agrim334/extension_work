package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class Expand
  extends Task
{
  public static final String NATIVE_ENCODING = "native-encoding";
  public static final String ERROR_MULTIPLE_MAPPERS = "Cannot define more than one mapper";
  private static final FileUtils FILE_UTILS = ;
  private static final int BUFFER_SIZE = 1024;
  private File dest;
  private File source;
  private boolean overwrite = true;
  private Mapper mapperElement = null;
  private List<PatternSet> patternsets = new Vector();
  private Union resources = new Union();
  private boolean resourcesSpecified = false;
  private boolean failOnEmptyArchive = false;
  private boolean stripAbsolutePathSpec = true;
  private boolean scanForUnicodeExtraFields = true;
  private Boolean allowFilesToEscapeDest = null;
  private String encoding;
  
  public Expand()
  {
    this("UTF8");
  }
  
  protected Expand(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void setFailOnEmptyArchive(boolean b)
  {
    failOnEmptyArchive = b;
  }
  
  public boolean getFailOnEmptyArchive()
  {
    return failOnEmptyArchive;
  }
  
  public void execute()
    throws BuildException
  {
    if ("expand".equals(getTaskType())) {
      log("!! expand is deprecated. Use unzip instead. !!");
    }
    if ((source == null) && (!resourcesSpecified)) {
      throw new BuildException("src attribute and/or resources must be specified");
    }
    if (dest == null) {
      throw new BuildException("Dest attribute must be specified");
    }
    if ((dest.exists()) && (!dest.isDirectory())) {
      throw new BuildException("Dest must be a directory.", getLocation());
    }
    if (source != null)
    {
      if (source.isDirectory()) {
        throw new BuildException("Src must not be a directory. Use nested filesets instead.", getLocation());
      }
      if (!source.exists()) {
        throw new BuildException("src '" + source + "' doesn't exist.");
      }
      if (!source.canRead()) {
        throw new BuildException("src '" + source + "' cannot be read.");
      }
      expandFile(FILE_UTILS, source, dest);
    }
    for (Resource r : resources) {
      if (!r.isExists())
      {
        log("Skipping '" + r.getName() + "' because it doesn't exist.");
      }
      else
      {
        FileProvider fp = (FileProvider)r.as(FileProvider.class);
        if (fp != null) {
          expandFile(FILE_UTILS, fp.getFile(), dest);
        } else {
          expandResource(r, dest);
        }
      }
    }
  }
  
  protected void expandFile(FileUtils fileUtils, File srcF, File dir)
  {
    log("Expanding: " + srcF + " into " + dir, 2);
    FileNameMapper mapper = getMapper();
    if (!srcF.exists()) {
      throw new BuildException("Unable to expand " + srcF + " as the file does not exist", getLocation());
    }
    try
    {
      ZipFile zf = new ZipFile(srcF, encoding, scanForUnicodeExtraFields);
      try
      {
        boolean empty = true;
        Enumeration<ZipEntry> entries = zf.getEntries();
        while (entries.hasMoreElements())
        {
          ZipEntry ze = (ZipEntry)entries.nextElement();
          empty = false;
          InputStream is = null;
          log("extracting " + ze.getName(), 4);
          try
          {
            extractFile(fileUtils, srcF, dir, 
              is = zf.getInputStream(ze), ze
              .getName(), new Date(ze.getTime()), ze
              .isDirectory(), mapper);
          }
          finally
          {
            FileUtils.close(is);
          }
        }
        if ((empty) && (getFailOnEmptyArchive())) {
          throw new BuildException("archive '%s' is empty", new Object[] { srcF });
        }
        log("expand complete", 3);
        zf.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        zf.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      throw new BuildException("Error while expanding " + srcF.getPath() + "\n" + ioe.toString(), ioe);
    }
  }
  
  protected void expandResource(Resource srcR, File dir)
  {
    throw new BuildException("only filesystem based resources are supported by this task.");
  }
  
  protected FileNameMapper getMapper()
  {
    if (mapperElement != null) {
      return mapperElement.getImplementation();
    }
    return new IdentityMapper();
  }
  
  protected void extractFile(FileUtils fileUtils, File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory, FileNameMapper mapper)
    throws IOException
  {
    boolean entryNameStartsWithPathSpec = (!entryName.isEmpty()) && ((entryName.charAt(0) == File.separatorChar) || (entryName.charAt(0) == '/') || (entryName.charAt(0) == '\\'));
    if ((stripAbsolutePathSpec) && (entryNameStartsWithPathSpec))
    {
      log("stripped absolute path spec from " + entryName, 3);
      
      entryName = entryName.substring(1);
    }
    boolean allowedOutsideOfDest = (Boolean.TRUE == getAllowFilesToEscapeDest()) || ((null == getAllowFilesToEscapeDest()) && (!stripAbsolutePathSpec) && (entryNameStartsWithPathSpec));
    if ((patternsets != null) && (!patternsets.isEmpty()))
    {
      String name = entryName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
      
      Set<String> includePatterns = new HashSet();
      Set<String> excludePatterns = new HashSet();
      for (Iterator localIterator = patternsets.iterator(); localIterator.hasNext();)
      {
        p = (PatternSet)localIterator.next();
        String[] incls = p.getIncludePatterns(getProject());
        if ((incls == null) || (incls.length == 0)) {
          incls = new String[] { "**" };
        }
        String[] arrayOfString1 = incls;int i = arrayOfString1.length;
        String incl;
        for (String str1 = 0; str1 < i; str1++)
        {
          incl = arrayOfString1[str1];
          
          String pattern = incl.replace('/', File.separatorChar).replace('\\', File.separatorChar);
          if (pattern.endsWith(File.separator)) {
            pattern = pattern + "**";
          }
          includePatterns.add(pattern);
        }
        String[] excls = p.getExcludePatterns(getProject());
        if (excls != null)
        {
          String[] arrayOfString2 = excls;str1 = arrayOfString2.length;
          for (incl = 0; incl < str1; incl++)
          {
            String excl = arrayOfString2[incl];
            
            String pattern = excl.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            if (pattern.endsWith(File.separator)) {
              pattern = pattern + "**";
            }
            excludePatterns.add(pattern);
          }
        }
      }
      PatternSet p;
      boolean included = false;
      for (String pattern : includePatterns) {
        if (SelectorUtils.matchPath(pattern, name))
        {
          included = true;
          break;
        }
      }
      for (String pattern : excludePatterns) {
        if (SelectorUtils.matchPath(pattern, name))
        {
          included = false;
          break;
        }
      }
      if (!included)
      {
        log("skipping " + entryName + " as it is excluded or not included.", 3);
        
        return;
      }
    }
    String[] mappedNames = mapper.mapFileName(entryName);
    if ((mappedNames == null) || (mappedNames.length == 0)) {
      mappedNames = new String[] { entryName };
    }
    File f = fileUtils.resolveFile(dir, mappedNames[0]);
    if ((!allowedOutsideOfDest) && (!fileUtils.isLeadingPath(dir, f, true)))
    {
      log("skipping " + entryName + " as its target " + f.getCanonicalPath() + " is outside of " + dir
        .getCanonicalPath() + ".", 3);
      return;
    }
    try
    {
      if ((!overwrite) && (f.exists()) && 
        (f.lastModified() >= entryDate.getTime()))
      {
        log("Skipping " + f + " as it is up-to-date", 4);
        
        return;
      }
      log("expanding " + entryName + " to " + f, 3);
      
      File dirF = f.getParentFile();
      if (dirF != null) {
        dirF.mkdirs();
      }
      if (isDirectory)
      {
        f.mkdirs();
      }
      else
      {
        byte[] buffer = new byte['Ѐ'];
        OutputStream fos = Files.newOutputStream(f.toPath(), new OpenOption[0]);
        try
        {
          int length;
          while ((length = compressedInputStream.read(buffer)) >= 0) {
            fos.write(buffer, 0, length);
          }
          if (fos == null) {
            break label946;
          }
          fos.close();
        }
        catch (Throwable localThrowable)
        {
          if (fos == null) {
            break label943;
          }
        }
        try
        {
          fos.close();
        }
        catch (Throwable localThrowable2)
        {
          localThrowable.addSuppressed(localThrowable2);
        }
        label943:
        throw localThrowable;
      }
      label946:
      fileUtils.setFileLastModified(f, entryDate.getTime());
    }
    catch (FileNotFoundException ex)
    {
      log("Unable to expand to file " + f.getPath(), ex, 1);
    }
  }
  
  public void setDest(File d)
  {
    dest = d;
  }
  
  public void setSrc(File s)
  {
    source = s;
  }
  
  public void setOverwrite(boolean b)
  {
    overwrite = b;
  }
  
  public void addPatternset(PatternSet set)
  {
    patternsets.add(set);
  }
  
  public void addFileset(FileSet set)
  {
    add(set);
  }
  
  public void add(ResourceCollection rc)
  {
    resourcesSpecified = true;
    resources.add(rc);
  }
  
  public Mapper createMapper()
    throws BuildException
  {
    if (mapperElement != null) {
      throw new BuildException("Cannot define more than one mapper", getLocation());
    }
    mapperElement = new Mapper(getProject());
    return mapperElement;
  }
  
  public void add(FileNameMapper fileNameMapper)
  {
    createMapper().add(fileNameMapper);
  }
  
  public void setEncoding(String encoding)
  {
    internalSetEncoding(encoding);
  }
  
  protected void internalSetEncoding(String encoding)
  {
    if ("native-encoding".equals(encoding)) {
      encoding = null;
    }
    this.encoding = encoding;
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public void setStripAbsolutePathSpec(boolean b)
  {
    stripAbsolutePathSpec = b;
  }
  
  public void setScanForUnicodeExtraFields(boolean b)
  {
    internalSetScanForUnicodeExtraFields(b);
  }
  
  protected void internalSetScanForUnicodeExtraFields(boolean b)
  {
    scanForUnicodeExtraFields = b;
  }
  
  public boolean getScanForUnicodeExtraFields()
  {
    return scanForUnicodeExtraFields;
  }
  
  public void setAllowFilesToEscapeDest(boolean b)
  {
    allowFilesToEscapeDest = Boolean.valueOf(b);
  }
  
  public Boolean getAllowFilesToEscapeDest()
  {
    return allowFilesToEscapeDest;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Expand
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */