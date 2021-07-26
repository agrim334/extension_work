package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

class EjbJar$Config
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.EjbJar.Config
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */