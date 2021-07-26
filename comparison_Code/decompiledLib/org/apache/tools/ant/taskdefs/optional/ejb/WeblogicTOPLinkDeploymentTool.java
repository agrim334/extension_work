package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;

public class WeblogicTOPLinkDeploymentTool
  extends WeblogicDeploymentTool
{
  private static final String TL_DTD_LOC = "http://www.objectpeople.com/tlwl/dtd/toplink-cmp_2_5_1.dtd";
  private String toplinkDescriptor;
  private String toplinkDTD;
  
  public void setToplinkdescriptor(String inString)
  {
    toplinkDescriptor = inString;
  }
  
  public void setToplinkdtd(String inString)
  {
    toplinkDTD = inString;
  }
  
  protected DescriptorHandler getDescriptorHandler(File srcDir)
  {
    DescriptorHandler handler = super.getDescriptorHandler(srcDir);
    if (toplinkDTD != null) {
      handler.registerDTD("-//The Object People, Inc.//DTD TOPLink for WebLogic CMP 2.5.1//EN", toplinkDTD);
    } else {
      handler.registerDTD("-//The Object People, Inc.//DTD TOPLink for WebLogic CMP 2.5.1//EN", "http://www.objectpeople.com/tlwl/dtd/toplink-cmp_2_5_1.dtd");
    }
    return handler;
  }
  
  protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix)
  {
    super.addVendorFiles(ejbFiles, ddPrefix);
    
    File toplinkDD = new File(getConfigdescriptorDir, ddPrefix + toplinkDescriptor);
    if (toplinkDD.exists()) {
      ejbFiles.put("META-INF/" + toplinkDescriptor, toplinkDD);
    } else {
      log("Unable to locate toplink deployment descriptor. It was expected to be in " + toplinkDD
        .getPath(), 1);
    }
  }
  
  public void validateConfigured()
    throws BuildException
  {
    super.validateConfigured();
    if (toplinkDescriptor == null) {
      throw new BuildException("The toplinkdescriptor attribute must be specified");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.WeblogicTOPLinkDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */