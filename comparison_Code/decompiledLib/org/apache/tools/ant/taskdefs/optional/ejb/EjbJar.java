package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

public class EjbJar
  extends MatchingTask
{
  static class Config
  {
    public File srcDir;
    public File descriptorDir;
    public String baseNameTerminator = "-";
    public String baseJarName;
    public boolean flatDestDir = false;
    public Path classpath;
    public List<FileSet> supportFileSets = new ArrayList();
    public ArrayList<EjbJar.DTDLocation> dtdLocations = new ArrayList();
    public EjbJar.NamingScheme namingScheme;
    public File manifest;
    public String analyzer;
  }
  
  public static class NamingScheme
    extends EnumeratedAttribute
  {
    public static final String EJB_NAME = "ejb-name";
    public static final String DIRECTORY = "directory";
    public static final String DESCRIPTOR = "descriptor";
    public static final String BASEJARNAME = "basejarname";
    
    public String[] getValues()
    {
      return new String[] { "ejb-name", "directory", "descriptor", "basejarname" };
    }
  }
  
  public static class CMPVersion
    extends EnumeratedAttribute
  {
    public static final String CMP1_0 = "1.0";
    public static final String CMP2_0 = "2.0";
    
    public String[] getValues()
    {
      return new String[] { "1.0", "2.0" };
    }
  }
  
  private Config config = new Config();
  private File destDir;
  private String genericJarSuffix = "-generic.jar";
  private String cmpVersion = "1.0";
  private List<EJBDeploymentTool> deploymentTools = new ArrayList();
  
  protected void addDeploymentTool(EJBDeploymentTool deploymentTool)
  {
    deploymentTool.setTask(this);
    deploymentTools.add(deploymentTool);
  }
  
  public OrionDeploymentTool createOrion()
  {
    OrionDeploymentTool tool = new OrionDeploymentTool();
    addDeploymentTool(tool);
    return tool;
  }
  
  public WeblogicDeploymentTool createWeblogic()
  {
    WeblogicDeploymentTool tool = new WeblogicDeploymentTool();
    addDeploymentTool(tool);
    return tool;
  }
  
  public WebsphereDeploymentTool createWebsphere()
  {
    WebsphereDeploymentTool tool = new WebsphereDeploymentTool();
    addDeploymentTool(tool);
    return tool;
  }
  
  public BorlandDeploymentTool createBorland()
  {
    log("Borland deployment tools", 3);
    
    BorlandDeploymentTool tool = new BorlandDeploymentTool();
    tool.setTask(this);
    deploymentTools.add(tool);
    return tool;
  }
  
  public IPlanetDeploymentTool createIplanet()
  {
    log("iPlanet Application Server deployment tools", 3);
    
    IPlanetDeploymentTool tool = new IPlanetDeploymentTool();
    addDeploymentTool(tool);
    return tool;
  }
  
  public JbossDeploymentTool createJboss()
  {
    JbossDeploymentTool tool = new JbossDeploymentTool();
    addDeploymentTool(tool);
    return tool;
  }
  
  public JonasDeploymentTool createJonas()
  {
    log("JOnAS deployment tools", 3);
    
    JonasDeploymentTool tool = new JonasDeploymentTool();
    addDeploymentTool(tool);
    return tool;
  }
  
  public WeblogicTOPLinkDeploymentTool createWeblogictoplink()
  {
    log("The <weblogictoplink> element is no longer required. Please use the <weblogic> element and set newCMP=\"true\"", 2);
    
    WeblogicTOPLinkDeploymentTool tool = new WeblogicTOPLinkDeploymentTool();
    
    addDeploymentTool(tool);
    return tool;
  }
  
  public Path createClasspath()
  {
    if (config.classpath == null) {
      config.classpath = new Path(getProject());
    }
    return config.classpath.createPath();
  }
  
  public DTDLocation createDTD()
  {
    DTDLocation dtdLocation = new DTDLocation();
    config.dtdLocations.add(dtdLocation);
    
    return dtdLocation;
  }
  
  public FileSet createSupport()
  {
    FileSet supportFileSet = new FileSet();
    config.supportFileSets.add(supportFileSet);
    return supportFileSet;
  }
  
  public void setManifest(File manifest)
  {
    config.manifest = manifest;
  }
  
  public void setSrcdir(File inDir)
  {
    config.srcDir = inDir;
  }
  
  public void setDescriptordir(File inDir)
  {
    config.descriptorDir = inDir;
  }
  
  public void setDependency(String analyzer)
  {
    config.analyzer = analyzer;
  }
  
  public void setBasejarname(String inValue)
  {
    config.baseJarName = inValue;
    if (config.namingScheme == null)
    {
      config.namingScheme = new NamingScheme();
      config.namingScheme.setValue("basejarname");
    }
    else if (!"basejarname".equals(config.namingScheme.getValue()))
    {
      throw new BuildException("The basejarname attribute is not compatible with the %s naming scheme", new Object[] {config.namingScheme.getValue() });
    }
  }
  
  public void setNaming(NamingScheme namingScheme)
  {
    config.namingScheme = namingScheme;
    if ((!"basejarname".equals(config.namingScheme.getValue())) && (config.baseJarName != null)) {
      throw new BuildException("The basejarname attribute is not compatible with the %s naming scheme", new Object[] {config.namingScheme.getValue() });
    }
  }
  
  public File getDestdir()
  {
    return destDir;
  }
  
  public void setDestdir(File inDir)
  {
    destDir = inDir;
  }
  
  public String getCmpversion()
  {
    return cmpVersion;
  }
  
  public void setCmpversion(CMPVersion version)
  {
    cmpVersion = version.getValue();
  }
  
  public void setClasspath(Path classpath)
  {
    config.classpath = classpath;
  }
  
  public void setFlatdestdir(boolean inValue)
  {
    config.flatDestDir = inValue;
  }
  
  public void setGenericjarsuffix(String inString)
  {
    genericJarSuffix = inString;
  }
  
  public void setBasenameterminator(String inValue)
  {
    config.baseNameTerminator = inValue;
  }
  
  private void validateConfig()
    throws BuildException
  {
    if (config.srcDir == null) {
      throw new BuildException("The srcDir attribute must be specified");
    }
    if (config.descriptorDir == null) {
      config.descriptorDir = config.srcDir;
    }
    if (config.namingScheme == null)
    {
      config.namingScheme = new NamingScheme();
      config.namingScheme.setValue("descriptor");
    }
    else if (("basejarname".equals(config.namingScheme.getValue())) && (config.baseJarName == null))
    {
      throw new BuildException("The basejarname attribute must be specified with the basejarname naming scheme");
    }
  }
  
  public void execute()
    throws BuildException
  {
    validateConfig();
    GenericDeploymentTool genericTool;
    if (deploymentTools.isEmpty())
    {
      genericTool = new GenericDeploymentTool();
      genericTool.setTask(this);
      genericTool.setDestdir(destDir);
      genericTool.setGenericJarSuffix(genericJarSuffix);
      deploymentTools.add(genericTool);
    }
    for (EJBDeploymentTool tool : deploymentTools)
    {
      tool.configure(config);
      tool.validateConfigured();
    }
    try
    {
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setValidating(true);
      SAXParser saxParser = saxParserFactory.newSAXParser();
      
      DirectoryScanner ds = getDirectoryScanner(config.descriptorDir);
      ds.scan();
      String[] files = ds.getIncludedFiles();
      
      log(files.length + " deployment descriptors located.", 3);
      String file;
      for (file : files) {
        for (EJBDeploymentTool tool : deploymentTools) {
          tool.processDescriptor(file, saxParser);
        }
      }
    }
    catch (SAXException se)
    {
      throw new BuildException("SAXException while creating parser.", se);
    }
    catch (ParserConfigurationException pce)
    {
      throw new BuildException("ParserConfigurationException while creating parser. ", pce);
    }
  }
  
  public static class DTDLocation
    extends DTDLocation
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.EjbJar
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */