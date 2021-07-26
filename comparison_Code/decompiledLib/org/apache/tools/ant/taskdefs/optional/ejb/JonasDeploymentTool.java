package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;

public class JonasDeploymentTool
  extends GenericDeploymentTool
{
  protected static final String EJB_JAR_1_1_PUBLIC_ID = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
  protected static final String EJB_JAR_2_0_PUBLIC_ID = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
  protected static final String JONAS_EJB_JAR_2_4_PUBLIC_ID = "-//ObjectWeb//DTD JOnAS 2.4//EN";
  protected static final String JONAS_EJB_JAR_2_5_PUBLIC_ID = "-//ObjectWeb//DTD JOnAS 2.5//EN";
  protected static final String RMI_ORB = "RMI";
  protected static final String JEREMIE_ORB = "JEREMIE";
  protected static final String DAVID_ORB = "DAVID";
  protected static final String EJB_JAR_1_1_DTD = "ejb-jar_1_1.dtd";
  protected static final String EJB_JAR_2_0_DTD = "ejb-jar_2_0.dtd";
  protected static final String JONAS_EJB_JAR_2_4_DTD = "jonas-ejb-jar_2_4.dtd";
  protected static final String JONAS_EJB_JAR_2_5_DTD = "jonas-ejb-jar_2_5.dtd";
  protected static final String JONAS_DD = "jonas-ejb-jar.xml";
  protected static final String GENIC_CLASS = "org.objectweb.jonas_ejb.genic.GenIC";
  protected static final String OLD_GENIC_CLASS_1 = "org.objectweb.jonas_ejb.tools.GenWholeIC";
  protected static final String OLD_GENIC_CLASS_2 = "org.objectweb.jonas_ejb.tools.GenIC";
  private String descriptorName;
  private String jonasDescriptorName;
  private File outputdir;
  private boolean keepgenerated = false;
  private boolean nocompil = false;
  private boolean novalidation = false;
  private String javac;
  private String javacopts;
  private String rmicopts;
  private boolean secpropag = false;
  private boolean verbose = false;
  private String additionalargs;
  private File jonasroot;
  private boolean keepgeneric = false;
  private String suffix = ".jar";
  private String orb;
  private boolean nogenic = false;
  
  public void setKeepgenerated(boolean aBoolean)
  {
    keepgenerated = aBoolean;
  }
  
  public void setAdditionalargs(String aString)
  {
    additionalargs = aString;
  }
  
  public void setNocompil(boolean aBoolean)
  {
    nocompil = aBoolean;
  }
  
  public void setNovalidation(boolean aBoolean)
  {
    novalidation = aBoolean;
  }
  
  public void setJavac(String aString)
  {
    javac = aString;
  }
  
  public void setJavacopts(String aString)
  {
    javacopts = aString;
  }
  
  public void setRmicopts(String aString)
  {
    rmicopts = aString;
  }
  
  public void setSecpropag(boolean aBoolean)
  {
    secpropag = aBoolean;
  }
  
  public void setVerbose(boolean aBoolean)
  {
    verbose = aBoolean;
  }
  
  public void setJonasroot(File aFile)
  {
    jonasroot = aFile;
  }
  
  public void setKeepgeneric(boolean aBoolean)
  {
    keepgeneric = aBoolean;
  }
  
  public void setJarsuffix(String aString)
  {
    suffix = aString;
  }
  
  public void setOrb(String aString)
  {
    orb = aString;
  }
  
  public void setNogenic(boolean aBoolean)
  {
    nogenic = aBoolean;
  }
  
  public void processDescriptor(String aDescriptorName, SAXParser saxParser)
  {
    descriptorName = aDescriptorName;
    
    log("JOnAS Deployment Tool processing: " + descriptorName, 3);
    
    super.processDescriptor(descriptorName, saxParser);
    if (outputdir != null)
    {
      log("Deleting temp output directory '" + outputdir + "'.", 3);
      deleteAllFiles(outputdir);
    }
  }
  
  protected void writeJar(String baseName, File jarfile, Hashtable<String, File> ejbFiles, String publicId)
    throws BuildException
  {
    File genericJarFile = super.getVendorOutputJarFile(baseName);
    super.writeJar(baseName, genericJarFile, ejbFiles, publicId);
    
    addGenICGeneratedFiles(genericJarFile, ejbFiles);
    
    super.writeJar(baseName, getVendorOutputJarFile(baseName), ejbFiles, publicId);
    if (!keepgeneric)
    {
      log("Deleting generic JAR " + genericJarFile.toString(), 3);
      genericJarFile.delete();
    }
  }
  
  protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix)
  {
    jonasDescriptorName = getJonasDescriptorName();
    File jonasDD = new File(getConfigdescriptorDir, jonasDescriptorName);
    if (jonasDD.exists()) {
      ejbFiles.put("META-INF/jonas-ejb-jar.xml", jonasDD);
    } else {
      log("Unable to locate the JOnAS deployment descriptor. It was expected to be in: " + jonasDD
        .getPath() + ".", 1);
    }
  }
  
  protected File getVendorOutputJarFile(String baseName)
  {
    return new File(getDestDir(), baseName + suffix);
  }
  
  private String getJonasDescriptorName()
  {
    boolean jonasConvention = false;
    
    int startOfFileName = descriptorName.lastIndexOf(File.separatorChar);
    String fileName;
    String path;
    String fileName;
    if (startOfFileName != -1)
    {
      String path = descriptorName.substring(0, startOfFileName + 1);
      fileName = descriptorName.substring(startOfFileName + 1);
    }
    else
    {
      path = "";
      fileName = descriptorName;
    }
    if (fileName.startsWith("ejb-jar.xml")) {
      return path + "jonas-ejb-jar.xml";
    }
    int endOfBaseName = descriptorName.indexOf(getConfigbaseNameTerminator, startOfFileName);
    if (endOfBaseName < 0)
    {
      endOfBaseName = descriptorName.lastIndexOf('.') - 1;
      if (endOfBaseName < 0) {
        endOfBaseName = descriptorName.length() - 1;
      }
      jonasConvention = true;
    }
    String baseName = descriptorName.substring(startOfFileName + 1, endOfBaseName + 1);
    String remainder = descriptorName.substring(endOfBaseName + 1);
    String jonasDN;
    String jonasDN;
    if (jonasConvention) {
      jonasDN = path + "jonas-" + baseName + ".xml";
    } else {
      jonasDN = path + baseName + "jonas-" + remainder;
    }
    log("Standard EJB descriptor name: " + descriptorName, 3);
    log("JOnAS-specific descriptor name: " + jonasDN, 3);
    
    return jonasDN;
  }
  
  protected String getJarBaseName(String descriptorFileName)
  {
    String baseName = null;
    if (getConfignamingScheme.getValue().equals("descriptor")) {
      if (!descriptorFileName.contains(getConfigbaseNameTerminator))
      {
        String aCanonicalDescriptor = descriptorFileName.replace('\\', '/');
        int lastSeparatorIndex = aCanonicalDescriptor.lastIndexOf('/');
        int endOfBaseName;
        int endOfBaseName;
        if (lastSeparatorIndex != -1) {
          endOfBaseName = descriptorFileName.indexOf(".xml", lastSeparatorIndex);
        } else {
          endOfBaseName = descriptorFileName.indexOf(".xml");
        }
        if (endOfBaseName != -1) {
          baseName = descriptorFileName.substring(0, endOfBaseName);
        }
      }
    }
    if (baseName == null) {
      baseName = super.getJarBaseName(descriptorFileName);
    }
    log("JAR base name: " + baseName, 3);
    
    return baseName;
  }
  
  protected void registerKnownDTDs(DescriptorHandler handler)
  {
    handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", jonasroot + File.separator + "xml" + File.separator + "ejb-jar_1_1.dtd");
    
    handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN", jonasroot + File.separator + "xml" + File.separator + "ejb-jar_2_0.dtd");
    
    handler.registerDTD("-//ObjectWeb//DTD JOnAS 2.4//EN", jonasroot + File.separator + "xml" + File.separator + "jonas-ejb-jar_2_4.dtd");
    
    handler.registerDTD("-//ObjectWeb//DTD JOnAS 2.5//EN", jonasroot + File.separator + "xml" + File.separator + "jonas-ejb-jar_2_5.dtd");
  }
  
  private void addGenICGeneratedFiles(File genericJarFile, Hashtable<String, File> ejbFiles)
  {
    if (nogenic) {
      return;
    }
    Java genicTask = new Java(getTask());
    genicTask.setTaskName("genic");
    genicTask.setFork(true);
    
    genicTask.createJvmarg().setValue("-Dinstall.root=" + jonasroot);
    
    String jonasConfigDir = jonasroot + File.separator + "config";
    File javaPolicyFile = new File(jonasConfigDir, "java.policy");
    if (javaPolicyFile.exists()) {
      genicTask.createJvmarg().setValue("-Djava.security.policy=" + javaPolicyFile
        .toString());
    }
    try
    {
      outputdir = createTempDir();
    }
    catch (IOException aIOException)
    {
      String msg = "Cannot create temp dir: " + aIOException.getMessage();
      throw new BuildException(msg, aIOException);
    }
    log("Using temporary output directory: " + outputdir, 3);
    
    genicTask.createArg().setValue("-d");
    genicTask.createArg().setFile(outputdir);
    for (String key : ejbFiles.keySet())
    {
      File f = new File(outputdir + File.separator + key);
      f.getParentFile().mkdirs();
    }
    log("Worked around a bug of GenIC 2.5.", 3);
    
    org.apache.tools.ant.types.Path classpath = getCombinedClasspath();
    if (classpath == null) {
      classpath = new org.apache.tools.ant.types.Path(getTask().getProject());
    }
    classpath.append(new org.apache.tools.ant.types.Path(classpath.getProject(), jonasConfigDir));
    classpath.append(new org.apache.tools.ant.types.Path(classpath.getProject(), outputdir.toString()));
    if (orb != null)
    {
      String orbJar = jonasroot + File.separator + "lib" + File.separator + orb + "_jonas.jar";
      
      classpath.append(new org.apache.tools.ant.types.Path(classpath.getProject(), orbJar));
    }
    log("Using classpath: " + classpath.toString(), 3);
    genicTask.setClasspath(classpath);
    
    String genicClass = getGenicClassName(classpath);
    if (genicClass == null)
    {
      log("Cannot find GenIC class in classpath.", 0);
      throw new BuildException("GenIC class not found, please check the classpath.");
    }
    log("Using '" + genicClass + "' GenIC class.", 3);
    genicTask.setClassname(genicClass);
    if (keepgenerated) {
      genicTask.createArg().setValue("-keepgenerated");
    }
    if (nocompil) {
      genicTask.createArg().setValue("-nocompil");
    }
    if (novalidation) {
      genicTask.createArg().setValue("-novalidation");
    }
    if (javac != null)
    {
      genicTask.createArg().setValue("-javac");
      genicTask.createArg().setLine(javac);
    }
    if ((javacopts != null) && (!javacopts.isEmpty()))
    {
      genicTask.createArg().setValue("-javacopts");
      genicTask.createArg().setLine(javacopts);
    }
    if ((rmicopts != null) && (!rmicopts.isEmpty()))
    {
      genicTask.createArg().setValue("-rmicopts");
      genicTask.createArg().setLine(rmicopts);
    }
    if (secpropag) {
      genicTask.createArg().setValue("-secpropag");
    }
    if (verbose) {
      genicTask.createArg().setValue("-verbose");
    }
    if (additionalargs != null) {
      genicTask.createArg().setValue(additionalargs);
    }
    genicTask.createArg().setValue("-noaddinjar");
    
    genicTask.createArg().setValue(genericJarFile.getPath());
    
    log("Calling " + genicClass + " for " + getConfigdescriptorDir + File.separator + descriptorName + ".", 3);
    if (genicTask.executeJava() != 0)
    {
      log("Deleting temp output directory '" + outputdir + "'.", 3);
      deleteAllFiles(outputdir);
      if (!keepgeneric)
      {
        log("Deleting generic JAR " + genericJarFile.toString(), 3);
        
        genericJarFile.delete();
      }
      throw new BuildException("GenIC reported an error.");
    }
    addAllFiles(outputdir, "", ejbFiles);
  }
  
  String getGenicClassName(org.apache.tools.ant.types.Path classpath)
  {
    log("Looking for GenIC class in classpath: " + classpath
      .toString(), 3);
    
    AntClassLoader cl = classpath.getProject().createClassLoader(classpath);
    try
    {
      cl.loadClass("org.objectweb.jonas_ejb.genic.GenIC");
      log("Found GenIC class 'org.objectweb.jonas_ejb.genic.GenIC' in classpath.", 3);
      
      String str = "org.objectweb.jonas_ejb.genic.GenIC";
      if (cl != null) {
        cl.close();
      }
      return str;
    }
    catch (ClassNotFoundException cnf1)
    {
      log("GenIC class 'org.objectweb.jonas_ejb.genic.GenIC' not found in classpath.", 3);
      try
      {
        cl.loadClass("org.objectweb.jonas_ejb.tools.GenWholeIC");
        log("Found GenIC class 'org.objectweb.jonas_ejb.tools.GenWholeIC' in classpath.", 3);
        
        cnf1 = "org.objectweb.jonas_ejb.tools.GenWholeIC";
        if (cl != null) {
          cl.close();
        }
        return (String)cnf1;
      }
      catch (ClassNotFoundException cnf2)
      {
        log("GenIC class 'org.objectweb.jonas_ejb.tools.GenWholeIC' not found in classpath.", 3);
        try
        {
          cl.loadClass("org.objectweb.jonas_ejb.tools.GenIC");
          log("Found GenIC class 'org.objectweb.jonas_ejb.tools.GenIC' in classpath.", 3);
          
          cnf2 = "org.objectweb.jonas_ejb.tools.GenIC";
          if (cl != null) {
            cl.close();
          }
          return (String)cnf2;
        }
        catch (ClassNotFoundException cnf3)
        {
          log("GenIC class 'org.objectweb.jonas_ejb.tools.GenIC' not found in classpath.", 3);
          if (cl == null) {
            break label187;
          }
        }
        cl.close();
      }
    }
    catch (Throwable localThrowable2)
    {
      if (cl == null) {
        break label185;
      }
    }
    try
    {
      cl.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2.addSuppressed(localThrowable1);
    }
    label185:
    throw localThrowable2;
    label187:
    return null;
  }
  
  protected void checkConfiguration(String descriptorFileName, SAXParser saxParser)
    throws BuildException
  {
    if (jonasroot == null) {
      throw new BuildException("The jonasroot attribute is not set.");
    }
    if (!jonasroot.isDirectory()) {
      throw new BuildException("The jonasroot attribute '%s' is not a valid directory.", new Object[] { jonasroot });
    }
    List<String> validOrbs = Arrays.asList(new String[] { "RMI", "JEREMIE", "DAVID" });
    if ((orb != null) && (!validOrbs.contains(orb))) {
      throw new BuildException("The orb attribute '%s' is not valid (must be one of %s.", new Object[] { orb, validOrbs });
    }
    if ((additionalargs != null) && (additionalargs.isEmpty())) {
      throw new BuildException("Empty additionalargs attribute.");
    }
    if ((javac != null) && (javac.isEmpty())) {
      throw new BuildException("Empty javac attribute.");
    }
  }
  
  private File createTempDir()
    throws IOException
  {
    return Files.createTempDirectory("genic", new FileAttribute[0]).toFile();
  }
  
  private void deleteAllFiles(File aFile)
  {
    if (aFile.isDirectory()) {
      for (File child : aFile.listFiles()) {
        deleteAllFiles(child);
      }
    }
    aFile.delete();
  }
  
  private void addAllFiles(File file, String rootDir, Hashtable<String, File> hashtable)
  {
    if (!file.exists()) {
      throw new IllegalArgumentException();
    }
    if (file.isDirectory()) {
      for (File child : file.listFiles())
      {
        String newRootDir;
        String newRootDir;
        if (rootDir.isEmpty()) {
          newRootDir = child.getName();
        } else {
          newRootDir = rootDir + File.separator + child.getName();
        }
        addAllFiles(child, newRootDir, hashtable);
      }
    } else {
      hashtable.put(rootDir, file);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.JonasDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */