package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;

public class OrionDeploymentTool
  extends GenericDeploymentTool
{
  protected static final String ORION_DD = "orion-ejb-jar.xml";
  private String jarSuffix = ".jar";
  
  protected void addVendorFiles(Hashtable<String, File> ejbFiles, String baseName)
  {
    String ddPrefix = usingBaseJarName() ? "" : baseName;
    File orionDD = new File(getConfigdescriptorDir, ddPrefix + "orion-ejb-jar.xml");
    if (orionDD.exists()) {
      ejbFiles.put("META-INF/orion-ejb-jar.xml", orionDD);
    } else {
      log("Unable to locate Orion deployment descriptor. It was expected to be in " + orionDD.getPath(), 1);
    }
  }
  
  File getVendorOutputJarFile(String baseName)
  {
    return new File(getDestDir(), baseName + jarSuffix);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.OrionDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */