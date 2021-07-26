package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipOutputStream;

public class War
  extends Jar
{
  private static final FileUtils FILE_UTILS = ;
  private static final String XML_DESCRIPTOR_PATH = "WEB-INF/web.xml";
  private File deploymentDescriptor;
  private boolean needxmlfile = true;
  private File addedWebXmlFile;
  
  public War()
  {
    archiveType = "war";
    emptyBehavior = "create";
  }
  
  @Deprecated
  public void setWarfile(File warFile)
  {
    setDestFile(warFile);
  }
  
  public void setWebxml(File descr)
  {
    deploymentDescriptor = descr;
    if (!deploymentDescriptor.exists()) {
      throw new BuildException("Deployment descriptor:  does not exist.", new Object[] { deploymentDescriptor });
    }
    ZipFileSet fs = new ZipFileSet();
    fs.setFile(deploymentDescriptor);
    fs.setFullpath("WEB-INF/web.xml");
    super.addFileset(fs);
  }
  
  public void setNeedxmlfile(boolean needxmlfile)
  {
    this.needxmlfile = needxmlfile;
  }
  
  public void addLib(ZipFileSet fs)
  {
    fs.setPrefix("WEB-INF/lib/");
    super.addFileset(fs);
  }
  
  public void addClasses(ZipFileSet fs)
  {
    fs.setPrefix("WEB-INF/classes/");
    super.addFileset(fs);
  }
  
  public void addWebinf(ZipFileSet fs)
  {
    fs.setPrefix("WEB-INF/");
    super.addFileset(fs);
  }
  
  protected void initZipOutputStream(ZipOutputStream zOut)
    throws IOException, BuildException
  {
    super.initZipOutputStream(zOut);
  }
  
  protected void zipFile(File file, ZipOutputStream zOut, String vPath, int mode)
    throws IOException
  {
    boolean addFile = true;
    if ("WEB-INF/web.xml".equalsIgnoreCase(vPath)) {
      if (addedWebXmlFile != null)
      {
        addFile = false;
        if (!FILE_UTILS.fileNameEquals(addedWebXmlFile, file)) {
          logWhenWriting("Warning: selected " + archiveType + " files include a second " + "WEB-INF/web.xml" + " which will be ignored.\nThe duplicate entry is at " + file + "\nThe file that will be used is " + addedWebXmlFile, 1);
        }
      }
      else
      {
        addedWebXmlFile = file;
        
        addFile = true;
        
        deploymentDescriptor = file;
      }
    }
    if (addFile) {
      super.zipFile(file, zOut, vPath, mode);
    }
  }
  
  protected void cleanUp()
  {
    if ((addedWebXmlFile == null) && (deploymentDescriptor == null) && (needxmlfile)) {
      if ((!isInUpdateMode()) && 
        (hasUpdatedFile())) {
        throw new BuildException("No WEB-INF/web.xml file was added.\nIf this is your intent, set needxmlfile='false' ");
      }
    }
    addedWebXmlFile = null;
    super.cleanUp();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.War
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */