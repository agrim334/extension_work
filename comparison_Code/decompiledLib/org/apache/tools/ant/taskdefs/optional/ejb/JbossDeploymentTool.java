package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;

public class JbossDeploymentTool
  extends GenericDeploymentTool
{
  protected static final String JBOSS_DD = "jboss.xml";
  protected static final String JBOSS_CMP10D = "jaws.xml";
  protected static final String JBOSS_CMP20D = "jbosscmp-jdbc.xml";
  private String jarSuffix = ".jar";
  
  public void setSuffix(String inString)
  {
    jarSuffix = inString;
  }
  
  protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix)
  {
    File jbossDD = new File(getConfigdescriptorDir, ddPrefix + "jboss.xml");
    if (jbossDD.exists())
    {
      ejbFiles.put("META-INF/jboss.xml", jbossDD);
    }
    else
    {
      log("Unable to locate jboss deployment descriptor. It was expected to be in " + jbossDD
        .getPath(), 1);
      return;
    }
    String descriptorFileName = "jaws.xml";
    if ("2.0".equals(getParent().getCmpversion())) {
      descriptorFileName = "jbosscmp-jdbc.xml";
    }
    File jbossCMPD = new File(getConfigdescriptorDir, ddPrefix + descriptorFileName);
    if (jbossCMPD.exists()) {
      ejbFiles.put("META-INF/" + descriptorFileName, jbossCMPD);
    } else {
      log("Unable to locate jboss cmp descriptor. It was expected to be in " + jbossCMPD
        .getPath(), 3);
    }
  }
  
  File getVendorOutputJarFile(String baseName)
  {
    if ((getDestDir() == null) && (getParent().getDestdir() == null)) {
      throw new BuildException("DestDir not specified");
    }
    if (getDestDir() == null) {
      return new File(getParent().getDestdir(), baseName + jarSuffix);
    }
    return new File(getDestDir(), baseName + jarSuffix);
  }
  
  public void validateConfigured()
    throws BuildException
  {}
  
  private EjbJar getParent()
  {
    return (EjbJar)getTask();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.JbossDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */