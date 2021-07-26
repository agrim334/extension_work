package org.apache.tools.ant.taskdefs.rmic;

import java.io.File;
import java.util.Random;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.StringUtils;

class DefaultRmicAdapter$RmicFileNameMapper
  implements FileNameMapper
{
  private DefaultRmicAdapter$RmicFileNameMapper(DefaultRmicAdapter paramDefaultRmicAdapter) {}
  
  public void setFrom(String s) {}
  
  public void setTo(String s) {}
  
  public String[] mapFileName(String name)
  {
    if ((name == null) || 
      (!name.endsWith(".class")) || 
      (name.endsWith(this$0.getStubClassSuffix() + ".class")) || 
      (name.endsWith(this$0.getSkelClassSuffix() + ".class")) || 
      (name.endsWith(this$0.getTieClassSuffix() + ".class"))) {
      return null;
    }
    String base = StringUtils.removeSuffix(name, ".class");
    
    String classname = base.replace(File.separatorChar, '.');
    if ((DefaultRmicAdapter.access$100(this$0).getVerify()) && 
      (!DefaultRmicAdapter.access$100(this$0).isValidRmiRemote(classname))) {
      return null;
    }
    String[] target = { name + ".tmp." + DefaultRmicAdapter.access$200().nextLong() };
    if ((!DefaultRmicAdapter.access$100(this$0).getIiop()) && (!DefaultRmicAdapter.access$100(this$0).getIdl()))
    {
      if ("1.2".equals(DefaultRmicAdapter.access$100(this$0).getStubVersion())) {
        target = new String[] {base + this$0.getStubClassSuffix() + ".class" };
      } else {
        target = new String[] {base + this$0.getStubClassSuffix() + ".class", base + this$0.getSkelClassSuffix() + ".class" };
      }
    }
    else if (!DefaultRmicAdapter.access$100(this$0).getIdl())
    {
      int lastSlash = base.lastIndexOf(File.separatorChar);
      
      int index = -1;
      String dirname;
      String dirname;
      if (lastSlash == -1)
      {
        index = 0;
        dirname = "";
      }
      else
      {
        index = lastSlash + 1;
        dirname = base.substring(0, index);
      }
      String filename = base.substring(index);
      try
      {
        Class<?> c = DefaultRmicAdapter.access$100(this$0).getLoader().loadClass(classname);
        if (c.isInterface())
        {
          target = new String[] {dirname + "_" + filename + this$0.getStubClassSuffix() + ".class" };
        }
        else
        {
          Class<?> interf = DefaultRmicAdapter.access$100(this$0).getRemoteInterface(c);
          String iName = interf.getName();
          
          int lastDot = iName.lastIndexOf('.');
          String iDir;
          int iIndex;
          String iDir;
          if (lastDot == -1)
          {
            int iIndex = 0;
            iDir = "";
          }
          else
          {
            iIndex = lastDot + 1;
            iDir = iName.substring(0, iIndex);
            iDir = iDir.replace('.', File.separatorChar);
          }
          target = new String[] {dirname + "_" + filename + this$0.getTieClassSuffix() + ".class", iDir + "_" + iName.substring(iIndex) + this$0.getStubClassSuffix() + ".class" };
        }
      }
      catch (ClassNotFoundException e)
      {
        DefaultRmicAdapter.access$100(this$0).log("Unable to verify class " + classname + ". It could not be found.", 1);
      }
      catch (NoClassDefFoundError e)
      {
        DefaultRmicAdapter.access$100(this$0).log("Unable to verify class " + classname + ". It is not defined.", 1);
      }
      catch (Throwable t)
      {
        DefaultRmicAdapter.access$100(this$0).log("Unable to verify class " + classname + ". Loading caused Exception: " + t
        
          .getMessage(), 1);
      }
    }
    return target;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter.RmicFileNameMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */