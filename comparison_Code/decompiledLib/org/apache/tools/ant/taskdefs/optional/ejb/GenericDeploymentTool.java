package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GenericDeploymentTool
  implements EJBDeploymentTool
{
  public static final int DEFAULT_BUFFER_SIZE = 1024;
  public static final int JAR_COMPRESS_LEVEL = 9;
  protected static final String META_DIR = "META-INF/";
  protected static final String MANIFEST = "META-INF/MANIFEST.MF";
  protected static final String EJB_DD = "ejb-jar.xml";
  public static final String ANALYZER_SUPER = "super";
  public static final String ANALYZER_FULL = "full";
  public static final String ANALYZER_NONE = "none";
  public static final String DEFAULT_ANALYZER = "super";
  public static final String ANALYZER_CLASS_SUPER = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
  public static final String ANALYZER_CLASS_FULL = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
  private EjbJar.Config config;
  private File destDir;
  private Path classpath;
  private String genericJarSuffix = "-generic.jar";
  private Task task;
  private ClassLoader classpathLoader = null;
  private Set<String> addedfiles;
  private DescriptorHandler handler;
  private DependencyAnalyzer dependencyAnalyzer;
  
  public void setDestdir(File inDir)
  {
    destDir = inDir;
  }
  
  protected File getDestDir()
  {
    return destDir;
  }
  
  public void setTask(Task task)
  {
    this.task = task;
  }
  
  protected Task getTask()
  {
    return task;
  }
  
  protected EjbJar.Config getConfig()
  {
    return config;
  }
  
  protected boolean usingBaseJarName()
  {
    return config.baseJarName != null;
  }
  
  public void setGenericJarSuffix(String inString)
  {
    genericJarSuffix = inString;
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(task.getProject());
    }
    return classpath.createPath();
  }
  
  public void setClasspath(Path classpath)
  {
    this.classpath = classpath;
  }
  
  protected Path getCombinedClasspath()
  {
    Path combinedPath = classpath;
    if (config.classpath != null) {
      if (combinedPath == null) {
        combinedPath = config.classpath;
      } else {
        combinedPath.append(config.classpath);
      }
    }
    return combinedPath;
  }
  
  protected void log(String message, int level)
  {
    getTask().log(message, level);
  }
  
  protected Location getLocation()
  {
    return getTask().getLocation();
  }
  
  private void createAnalyzer()
  {
    String analyzer = config.analyzer;
    if (analyzer == null) {
      analyzer = "super";
    }
    if (analyzer.equals("none")) {
      return;
    }
    String analyzerClassName = null;
    switch (analyzer)
    {
    case "super": 
      analyzerClassName = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
      break;
    case "full": 
      analyzerClassName = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
      break;
    default: 
      analyzerClassName = analyzer;
    }
    try
    {
      Object analyzerClass = Class.forName(analyzerClassName).asSubclass(DependencyAnalyzer.class);
      dependencyAnalyzer = ((DependencyAnalyzer)((Class)analyzerClass).newInstance());
      dependencyAnalyzer.addClassPath(new Path(task.getProject(), config.srcDir
        .getPath()));
      dependencyAnalyzer.addClassPath(config.classpath);
    }
    catch (NoClassDefFoundError e)
    {
      dependencyAnalyzer = null;
      task.log("Unable to load dependency analyzer: " + analyzerClassName + " - dependent class not found: " + e
        .getMessage(), 1);
    }
    catch (Exception e)
    {
      dependencyAnalyzer = null;
      task.log("Unable to load dependency analyzer: " + analyzerClassName + " - exception: " + e
        .getMessage(), 1);
    }
  }
  
  public void configure(EjbJar.Config config)
  {
    this.config = config;
    
    createAnalyzer();
    classpathLoader = null;
  }
  
  protected void addFileToJar(JarOutputStream jStream, File inputFile, String logicalFilename)
    throws BuildException
  {
    if (!addedfiles.contains(logicalFilename)) {
      try
      {
        InputStream iStream = Files.newInputStream(inputFile.toPath(), new OpenOption[0]);
        try
        {
          ZipEntry zipEntry = new ZipEntry(logicalFilename.replace('\\', '/'));
          jStream.putNextEntry(zipEntry);
          
          byte[] byteBuffer = new byte['ࠀ'];
          int count = 0;
          do
          {
            jStream.write(byteBuffer, 0, count);
            count = iStream.read(byteBuffer, 0, byteBuffer.length);
          } while (count != -1);
          addedfiles.add(logicalFilename);
          if (iStream == null) {
            return;
          }
          iStream.close();
        }
        catch (Throwable localThrowable)
        {
          if (iStream == null) {
            break label135;
          }
        }
        try
        {
          iStream.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        label135:
        throw localThrowable;
      }
      catch (IOException ioe)
      {
        log("WARNING: IOException while adding entry " + logicalFilename + " to jarfile from " + inputFile
          .getPath() + " " + ioe
          .getClass().getName() + "-" + ioe.getMessage(), 1);
      }
    }
  }
  
  protected DescriptorHandler getDescriptorHandler(File srcDir)
  {
    DescriptorHandler h = new DescriptorHandler(getTask(), srcDir);
    
    registerKnownDTDs(h);
    for (EjbJar.DTDLocation dtdLocation : getConfigdtdLocations) {
      h.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
    }
    return h;
  }
  
  protected void registerKnownDTDs(DescriptorHandler handler) {}
  
  public void processDescriptor(String descriptorFileName, SAXParser saxParser)
  {
    checkConfiguration(descriptorFileName, saxParser);
    try
    {
      handler = getDescriptorHandler(config.srcDir);
      
      Hashtable<String, File> ejbFiles = parseEjbFiles(descriptorFileName, saxParser);
      
      addSupportClasses(ejbFiles);
      
      String baseName = getJarBaseName(descriptorFileName);
      
      String ddPrefix = getVendorDDPrefix(baseName, descriptorFileName);
      
      File manifestFile = getManifestFile(ddPrefix);
      if (manifestFile != null) {
        ejbFiles.put("META-INF/MANIFEST.MF", manifestFile);
      }
      ejbFiles.put("META-INF/ejb-jar.xml", new File(config.descriptorDir, descriptorFileName));
      
      addVendorFiles(ejbFiles, ddPrefix);
      
      checkAndAddDependants(ejbFiles);
      if ((config.flatDestDir) && (!baseName.isEmpty()))
      {
        int startName = baseName.lastIndexOf(File.separator);
        if (startName == -1) {
          startName = 0;
        }
        int endName = baseName.length();
        baseName = baseName.substring(startName, endName);
      }
      File jarFile = getVendorOutputJarFile(baseName);
      if (needToRebuild(ejbFiles, jarFile))
      {
        log("building " + jarFile
          .getName() + " with " + 
          
          String.valueOf(ejbFiles.size()) + " files", 2);
        
        String publicId = getPublicId();
        writeJar(baseName, jarFile, ejbFiles, publicId);
      }
      else
      {
        log(jarFile.toString() + " is up to date.", 3);
      }
    }
    catch (SAXException se)
    {
      throw new BuildException("SAXException while parsing '" + descriptorFileName + "'. This probably indicates badly-formed XML.  Details: " + se.getMessage(), se);
    }
    catch (IOException ioe)
    {
      throw new BuildException("IOException while parsing'" + descriptorFileName + "'.  This probably indicates that the descriptor doesn't exist. Details: " + ioe.getMessage(), ioe);
    }
  }
  
  protected void checkConfiguration(String descriptorFileName, SAXParser saxParser)
    throws BuildException
  {}
  
  protected Hashtable<String, File> parseEjbFiles(String descriptorFileName, SAXParser saxParser)
    throws IOException, SAXException
  {
    InputStream descriptorStream = Files.newInputStream(new File(config.descriptorDir, descriptorFileName)
      .toPath(), new OpenOption[0]);
    try
    {
      saxParser.parse(new InputSource(descriptorStream), handler);
      Hashtable localHashtable = handler.getFiles();
      if (descriptorStream != null) {
        descriptorStream.close();
      }
      return localHashtable;
    }
    catch (Throwable localThrowable2)
    {
      if (descriptorStream != null) {
        try
        {
          descriptorStream.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable2.addSuppressed(localThrowable1);
        }
      }
      throw localThrowable2;
    }
  }
  
  protected void addSupportClasses(Hashtable<String, File> ejbFiles)
  {
    Project project = task.getProject();
    for (FileSet supportFileSet : config.supportFileSets)
    {
      File supportBaseDir = supportFileSet.getDir(project);
      DirectoryScanner supportScanner = supportFileSet.getDirectoryScanner(project);
      for (String supportFile : supportScanner.getIncludedFiles()) {
        ejbFiles.put(supportFile, new File(supportBaseDir, supportFile));
      }
    }
  }
  
  protected String getJarBaseName(String descriptorFileName)
  {
    String baseName = "";
    if ("basejarname".equals(config.namingScheme.getValue()))
    {
      String canonicalDescriptor = descriptorFileName.replace('\\', '/');
      int index = canonicalDescriptor.lastIndexOf('/');
      if (index != -1) {
        baseName = descriptorFileName.substring(0, index + 1);
      }
      baseName = baseName + config.baseJarName;
    }
    else if ("descriptor".equals(config.namingScheme.getValue()))
    {
      int lastSeparatorIndex = descriptorFileName.lastIndexOf(File.separator);
      int endBaseName = -1;
      if (lastSeparatorIndex != -1) {
        endBaseName = descriptorFileName.indexOf(config.baseNameTerminator, lastSeparatorIndex);
      } else {
        endBaseName = descriptorFileName.indexOf(config.baseNameTerminator);
      }
      if (endBaseName != -1) {
        baseName = descriptorFileName.substring(0, endBaseName);
      } else {
        throw new BuildException("Unable to determine jar name from descriptor \"%s\"", new Object[] { descriptorFileName });
      }
    }
    else if ("directory".equals(config.namingScheme.getValue()))
    {
      File descriptorFile = new File(config.descriptorDir, descriptorFileName);
      String path = descriptorFile.getAbsolutePath();
      
      int lastSeparatorIndex = path.lastIndexOf(File.separator);
      if (lastSeparatorIndex == -1) {
        throw new BuildException("Unable to determine directory name holding descriptor");
      }
      String dirName = path.substring(0, lastSeparatorIndex);
      int dirSeparatorIndex = dirName.lastIndexOf(File.separator);
      if (dirSeparatorIndex != -1) {
        dirName = dirName.substring(dirSeparatorIndex + 1);
      }
      baseName = dirName;
    }
    else if ("ejb-name".equals(config.namingScheme.getValue()))
    {
      baseName = handler.getEjbName();
    }
    return baseName;
  }
  
  public String getVendorDDPrefix(String baseName, String descriptorFileName)
  {
    String ddPrefix = null;
    if (config.namingScheme.getValue().equals("descriptor"))
    {
      ddPrefix = baseName + config.baseNameTerminator;
    }
    else if ((config.namingScheme.getValue().equals("basejarname")) || 
      (config.namingScheme.getValue().equals("ejb-name")) || 
      (config.namingScheme.getValue().equals("directory")))
    {
      String canonicalDescriptor = descriptorFileName.replace('\\', '/');
      int index = canonicalDescriptor.lastIndexOf('/');
      if (index == -1) {
        ddPrefix = "";
      } else {
        ddPrefix = descriptorFileName.substring(0, index + 1);
      }
    }
    return ddPrefix;
  }
  
  protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix) {}
  
  File getVendorOutputJarFile(String baseName)
  {
    return new File(destDir, baseName + genericJarSuffix);
  }
  
  protected boolean needToRebuild(Hashtable<String, File> ejbFiles, File jarFile)
  {
    if (jarFile.exists())
    {
      long lastBuild = jarFile.lastModified();
      for (File currentFile : ejbFiles.values()) {
        if (lastBuild < currentFile.lastModified())
        {
          log("Build needed because " + currentFile.getPath() + " is out of date", 3);
          
          return true;
        }
      }
      return false;
    }
    return true;
  }
  
  protected String getPublicId()
  {
    return handler.getPublicId();
  }
  
  protected File getManifestFile(String prefix)
  {
    File manifestFile = new File(getConfigdescriptorDir, prefix + "manifest.mf");
    if (manifestFile.exists()) {
      return manifestFile;
    }
    if (config.manifest != null) {
      return config.manifest;
    }
    return null;
  }
  
  protected void writeJar(String baseName, File jarfile, Hashtable<String, File> files, String publicId)
    throws BuildException
  {
    if (addedfiles == null) {
      addedfiles = new HashSet();
    } else {
      addedfiles.clear();
    }
    try
    {
      if (jarfile.exists()) {
        jarfile.delete();
      }
      jarfile.getParentFile().mkdirs();
      jarfile.createNewFile();
      
      InputStream in = null;
      Manifest manifest = null;
      String defaultManifest;
      try
      {
        File manifestFile = (File)files.get("META-INF/MANIFEST.MF");
        if ((manifestFile != null) && (manifestFile.exists()))
        {
          in = Files.newInputStream(manifestFile.toPath(), new OpenOption[0]);
        }
        else
        {
          defaultManifest = "/org/apache/tools/ant/defaultManifest.mf";
          in = getClass().getResourceAsStream(defaultManifest);
          if (in == null) {
            throw new BuildException("Could not find default manifest: %s", new Object[] { defaultManifest });
          }
        }
        manifest = new Manifest(in);
      }
      catch (IOException e)
      {
        throw new BuildException("Unable to read manifest", e, getLocation());
      }
      finally
      {
        if (in != null) {
          in.close();
        }
      }
      JarOutputStream jarStream = new JarOutputStream(Files.newOutputStream(jarfile.toPath(), new OpenOption[0]), manifest);
      try
      {
        jarStream.setMethod(8);
        for (Object entryFiles : files.entrySet())
        {
          String entryName = (String)((Map.Entry)entryFiles).getKey();
          if (!entryName.equals("META-INF/MANIFEST.MF"))
          {
            File entryFile = (File)((Map.Entry)entryFiles).getValue();
            log("adding file '" + entryName + "'", 3);
            addFileToJar(jarStream, entryFile, entryName);
            
            InnerClassFilenameFilter flt = new InnerClassFilenameFilter(entryFile.getName());
            File entryDir = entryFile.getParentFile();
            String[] innerfiles = entryDir.list(flt);
            if (innerfiles != null) {
              for (String innerfile : innerfiles)
              {
                int entryIndex = entryName.lastIndexOf(entryFile.getName()) - 1;
                if (entryIndex < 0) {
                  entryName = innerfile;
                } else {
                  entryName = entryName.substring(0, entryIndex) + File.separatorChar + innerfile;
                }
                entryFile = new File(config.srcDir, entryName);
                
                log("adding innerclass file '" + entryName + "'", 3);
                
                addFileToJar(jarStream, entryFile, entryName);
              }
            }
          }
        }
        jarStream.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        jarStream.close();
      }
      catch (Throwable localThrowable2)
      {
        localThrowable.addSuppressed(localThrowable2);
      }
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      String msg = "IOException while processing ejb-jar file '" + jarfile.toString() + "'. Details: " + ioe.getMessage();
      throw new BuildException(msg, ioe);
    }
  }
  
  protected void checkAndAddDependants(Hashtable<String, File> checkEntries)
    throws BuildException
  {
    if (dependencyAnalyzer == null) {
      return;
    }
    dependencyAnalyzer.reset();
    for (String entryName : checkEntries.keySet()) {
      if (entryName.endsWith(".class"))
      {
        String className = entryName.substring(0, entryName
          .length() - ".class".length());
        className = className.replace(File.separatorChar, '/');
        className = className.replace('/', '.');
        
        dependencyAnalyzer.addRootClass(className);
      }
    }
    for (String classname : Collections.list(dependencyAnalyzer.getClassDependencies()))
    {
      String location = classname.replace('.', File.separatorChar) + ".class";
      File classFile = new File(config.srcDir, location);
      if (classFile.exists())
      {
        checkEntries.put(location, classFile);
        log("dependent class: " + classname + " - " + classFile, 3);
      }
    }
  }
  
  protected ClassLoader getClassLoaderForBuild()
  {
    if (classpathLoader != null) {
      return classpathLoader;
    }
    Path combinedClasspath = getCombinedClasspath();
    if (combinedClasspath == null) {
      classpathLoader = getClass().getClassLoader();
    } else {
      classpathLoader = getTask().getProject().createClassLoader(combinedClasspath);
    }
    return classpathLoader;
  }
  
  public void validateConfigured()
    throws BuildException
  {
    if ((destDir == null) || (!destDir.isDirectory())) {
      throw new BuildException("A valid destination directory must be specified using the \"destdir\" attribute.", getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */