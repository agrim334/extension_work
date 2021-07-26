package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import java.util.Locale;
import org.apache.tools.ant.BuildException;

public class Os
  implements Condition
{
  private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
  private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
  private static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.ENGLISH);
  private static final String PATH_SEP = File.pathSeparator;
  public static final String FAMILY_WINDOWS = "windows";
  public static final String FAMILY_9X = "win9x";
  public static final String FAMILY_NT = "winnt";
  public static final String FAMILY_OS2 = "os/2";
  public static final String FAMILY_NETWARE = "netware";
  public static final String FAMILY_DOS = "dos";
  public static final String FAMILY_MAC = "mac";
  public static final String FAMILY_TANDEM = "tandem";
  public static final String FAMILY_UNIX = "unix";
  public static final String FAMILY_VMS = "openvms";
  public static final String FAMILY_ZOS = "z/os";
  public static final String FAMILY_OS400 = "os/400";
  private static final String DARWIN = "darwin";
  private String family;
  private String name;
  private String version;
  private String arch;
  
  public Os() {}
  
  public Os(String family)
  {
    setFamily(family);
  }
  
  public void setFamily(String f)
  {
    family = f.toLowerCase(Locale.ENGLISH);
  }
  
  public void setName(String name)
  {
    this.name = name.toLowerCase(Locale.ENGLISH);
  }
  
  public void setArch(String arch)
  {
    this.arch = arch.toLowerCase(Locale.ENGLISH);
  }
  
  public void setVersion(String version)
  {
    this.version = version.toLowerCase(Locale.ENGLISH);
  }
  
  public boolean eval()
    throws BuildException
  {
    return isOs(family, name, arch, version);
  }
  
  public static boolean isFamily(String family)
  {
    return isOs(family, null, null, null);
  }
  
  public static boolean isName(String name)
  {
    return isOs(null, name, null, null);
  }
  
  public static boolean isArch(String arch)
  {
    return isOs(null, null, arch, null);
  }
  
  public static boolean isVersion(String version)
  {
    return isOs(null, null, null, version);
  }
  
  public static boolean isOs(String family, String name, String arch, String version)
  {
    boolean retValue = false;
    if ((family != null) || (name != null) || (arch != null) || (version != null))
    {
      boolean isFamily = true;
      boolean isName = true;
      boolean isArch = true;
      boolean isVersion = true;
      if (family != null)
      {
        boolean isWindows = OS_NAME.contains("windows");
        boolean is9x = false;
        boolean isNT = false;
        if (isWindows)
        {
          is9x = (OS_NAME.contains("95")) || (OS_NAME.contains("98")) || (OS_NAME.contains("me")) || (OS_NAME.contains("ce"));
          isNT = !is9x;
        }
        switch (family)
        {
        case "windows": 
          isFamily = isWindows;
          break;
        case "win9x": 
          isFamily = (isWindows) && (is9x);
          break;
        case "winnt": 
          isFamily = (isWindows) && (isNT);
          break;
        case "os/2": 
          isFamily = OS_NAME.contains("os/2");
          break;
        case "netware": 
          isFamily = OS_NAME.contains("netware");
          break;
        case "dos": 
          isFamily = (PATH_SEP.equals(";")) && (!isFamily("netware"));
          break;
        case "mac": 
          isFamily = (OS_NAME.contains("mac")) || (OS_NAME.contains("darwin"));
          break;
        case "tandem": 
          isFamily = OS_NAME.contains("nonstop_kernel");
          break;
        case "unix": 
          isFamily = (PATH_SEP.equals(":")) && (!isFamily("openvms")) && ((!isFamily("mac")) || (OS_NAME.endsWith("x")) || (OS_NAME.contains("darwin")));
          break;
        case "z/os": 
          isFamily = (OS_NAME.contains("z/os")) || (OS_NAME.contains("os/390"));
          break;
        case "os/400": 
          isFamily = OS_NAME.contains("os/400");
          break;
        case "openvms": 
          isFamily = OS_NAME.contains("openvms");
          break;
        default: 
          throw new BuildException("Don't know how to detect os family \"" + family + "\"");
        }
      }
      if (name != null) {
        isName = name.equals(OS_NAME);
      }
      if (arch != null) {
        isArch = arch.equals(OS_ARCH);
      }
      if (version != null) {
        isVersion = version.equals(OS_VERSION);
      }
      retValue = (isFamily) && (isName) && (isArch) && (isVersion);
    }
    return retValue;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Os
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */