package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

public class IPlanetDeploymentTool
  extends GenericDeploymentTool
{
  private File iashome;
  private String jarSuffix = ".jar";
  private boolean keepgenerated = false;
  private boolean debug = false;
  private String descriptorName;
  private String iasDescriptorName;
  private String displayName;
  private static final String IAS_DD = "ias-ejb-jar.xml";
  
  public void setIashome(File iashome)
  {
    this.iashome = iashome;
  }
  
  public void setKeepgenerated(boolean keepgenerated)
  {
    this.keepgenerated = keepgenerated;
  }
  
  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }
  
  public void setSuffix(String jarSuffix)
  {
    this.jarSuffix = jarSuffix;
  }
  
  public void setGenericJarSuffix(String inString)
  {
    log("Since a generic JAR file is not created during processing, the iPlanet Deployment Tool does not support the \"genericjarsuffix\" attribute.  It will be ignored.", 1);
  }
  
  public void processDescriptor(String descriptorName, SAXParser saxParser)
  {
    this.descriptorName = descriptorName;
    iasDescriptorName = null;
    
    log("iPlanet Deployment Tool processing: " + descriptorName + " (and " + 
      getIasDescriptorName() + ")", 3);
    
    super.processDescriptor(descriptorName, saxParser);
  }
  
  protected void checkConfiguration(String descriptorFileName, SAXParser saxParser)
    throws BuildException
  {
    int startOfName = descriptorFileName.lastIndexOf(File.separatorChar) + 1;
    String stdXml = descriptorFileName.substring(startOfName);
    if ((stdXml.equals("ejb-jar.xml")) && (getConfigbaseJarName == null)) {
      throw new BuildException("No name specified for the completed JAR file.  The EJB descriptor should be prepended with the JAR name or it should be specified using the attribute \"basejarname\" in the \"ejbjar\" task.", getLocation());
    }
    File iasDescriptor = new File(getConfigdescriptorDir, getIasDescriptorName());
    if ((!iasDescriptor.exists()) || (!iasDescriptor.isFile())) {
      throw new BuildException("The iAS-specific EJB descriptor (" + iasDescriptor + ") was not found.", getLocation());
    }
    if ((iashome != null) && (!iashome.isDirectory())) {
      throw new BuildException("If \"iashome\" is specified, it must be a valid directory (it was set to " + iashome + ").", getLocation());
    }
  }
  
  protected Hashtable<String, File> parseEjbFiles(String descriptorFileName, SAXParser saxParser)
    throws IOException, SAXException
  {
    IPlanetEjbc ejbc = new IPlanetEjbc(new File(getConfigdescriptorDir, descriptorFileName), new File(getConfigdescriptorDir, getIasDescriptorName()), getConfigsrcDir, getCombinedClasspath().toString(), saxParser);
    
    ejbc.setRetainSource(keepgenerated);
    ejbc.setDebugOutput(debug);
    if (iashome != null) {
      ejbc.setIasHomeDir(iashome);
    }
    if (getConfigdtdLocations != null) {
      for (EjbJar.DTDLocation dtdLocation : getConfigdtdLocations) {
        ejbc.registerDTD(dtdLocation.getPublicId(), dtdLocation
          .getLocation());
      }
    }
    try
    {
      ejbc.execute();
    }
    catch (IPlanetEjbc.EjbcException e)
    {
      throw new BuildException("An error has occurred while trying to execute the iAS ejbc utility", e, getLocation());
    }
    displayName = ejbc.getDisplayName();
    Hashtable<String, File> files = ejbc.getEjbFiles();
    
    String[] cmpDescriptors = ejbc.getCmpDescriptors();
    if (cmpDescriptors.length > 0)
    {
      File baseDir = getConfigdescriptorDir;
      
      int endOfPath = descriptorFileName.lastIndexOf(File.separator);
      String relativePath = descriptorFileName.substring(0, endOfPath + 1);
      for (String descriptor : cmpDescriptors)
      {
        int endOfCmp = descriptor.lastIndexOf('/');
        String cmpDescriptor = descriptor.substring(endOfCmp + 1);
        
        File cmpFile = new File(baseDir, relativePath + cmpDescriptor);
        if (!cmpFile.exists()) {
          throw new BuildException("The CMP descriptor file (" + cmpFile + ") could not be found.", getLocation());
        }
        files.put(descriptor, cmpFile);
      }
    }
    return files;
  }
  
  protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix)
  {
    ejbFiles.put("META-INF/ias-ejb-jar.xml", new File(getConfigdescriptorDir, 
      getIasDescriptorName()));
  }
  
  File getVendorOutputJarFile(String baseName)
  {
    File jarFile = new File(getDestDir(), baseName + jarSuffix);
    log("JAR file name: " + jarFile.toString(), 3);
    return jarFile;
  }
  
  protected String getPublicId()
  {
    return null;
  }
  
  private String getIasDescriptorName()
  {
    if (iasDescriptorName != null) {
      return iasDescriptorName;
    }
    String path = "";
    
    int startOfFileName = descriptorName.lastIndexOf(File.separatorChar);
    if (startOfFileName != -1) {
      path = descriptorName.substring(0, startOfFileName + 1);
    }
    String remainder;
    String basename;
    String remainder;
    if (descriptorName.substring(startOfFileName + 1).equals("ejb-jar.xml"))
    {
      String basename = "";
      remainder = "ejb-jar.xml";
    }
    else
    {
      int endOfBaseName = descriptorName.indexOf(
        getConfigbaseNameTerminator, startOfFileName);
      if (endOfBaseName < 0)
      {
        endOfBaseName = descriptorName.lastIndexOf('.') - 1;
        if (endOfBaseName < 0) {
          endOfBaseName = descriptorName.length() - 1;
        }
      }
      basename = descriptorName.substring(startOfFileName + 1, endOfBaseName + 1);
      
      remainder = descriptorName.substring(endOfBaseName + 1);
    }
    iasDescriptorName = (path + basename + "ias-" + remainder);
    return iasDescriptorName;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.IPlanetDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */