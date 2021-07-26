package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipOutputStream;

public class Ear
  extends Jar
{
  private static final FileUtils FILE_UTILS = ;
  private File deploymentDescriptor;
  private boolean descriptorAdded;
  private static final String XML_DESCRIPTOR_PATH = "META-INF/application.xml";
  
  public Ear()
  {
    archiveType = "ear";
    emptyBehavior = "create";
  }
  
  @Deprecated
  public void setEarfile(File earFile)
  {
    setDestFile(earFile);
  }
  
  public void setAppxml(File descr)
  {
    deploymentDescriptor = descr;
    if (!deploymentDescriptor.exists()) {
      throw new BuildException("Deployment descriptor: %s does not exist.", new Object[] { deploymentDescriptor });
    }
    ZipFileSet fs = new ZipFileSet();
    fs.setFile(deploymentDescriptor);
    fs.setFullpath("META-INF/application.xml");
    super.addFileset(fs);
  }
  
  public void addArchives(ZipFileSet fs)
  {
    fs.setPrefix("/");
    super.addFileset(fs);
  }
  
  protected void initZipOutputStream(ZipOutputStream zOut)
    throws IOException, BuildException
  {
    if ((deploymentDescriptor == null) && (!isInUpdateMode())) {
      throw new BuildException("appxml attribute is required", getLocation());
    }
    super.initZipOutputStream(zOut);
  }
  
  protected void zipFile(File file, ZipOutputStream zOut, String vPath, int mode)
    throws IOException
  {
    if ("META-INF/application.xml".equalsIgnoreCase(vPath))
    {
      if ((deploymentDescriptor == null) || 
        (!FILE_UTILS.fileNameEquals(deploymentDescriptor, file)) || (descriptorAdded))
      {
        logWhenWriting("Warning: selected " + archiveType + " files include a " + "META-INF/application.xml" + " which will be ignored (please use appxml attribute to " + archiveType + " task)", 1);
      }
      else
      {
        super.zipFile(file, zOut, vPath, mode);
        descriptorAdded = true;
      }
    }
    else {
      super.zipFile(file, zOut, vPath, mode);
    }
  }
  
  protected void cleanUp()
  {
    descriptorAdded = false;
    super.cleanUp();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Ear
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */