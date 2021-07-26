package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class Native2AsciiAdapterFactory
{
  public static String getDefault()
  {
    if (shouldUseKaffe()) {
      return "kaffe";
    }
    return "builtin";
  }
  
  public static Native2AsciiAdapter getAdapter(String choice, ProjectComponent log)
    throws BuildException
  {
    return getAdapter(choice, log, null);
  }
  
  public static Native2AsciiAdapter getAdapter(String choice, ProjectComponent log, Path classpath)
    throws BuildException
  {
    if (((shouldUseKaffe()) && (choice == null)) || 
      ("kaffe".equals(choice))) {
      return new KaffeNative2Ascii();
    }
    if ("sun".equals(choice)) {
      return new SunNative2Ascii();
    }
    if ("builtin".equals(choice)) {
      return new BuiltinNative2Ascii();
    }
    if (choice != null) {
      return resolveClassName(choice, log
      
        .getProject()
        .createClassLoader(classpath));
    }
    return new BuiltinNative2Ascii();
  }
  
  private static Native2AsciiAdapter resolveClassName(String className, ClassLoader loader)
    throws BuildException
  {
    return (Native2AsciiAdapter)ClasspathUtils.newInstance(className, 
      loader != null ? loader : 
      Native2AsciiAdapterFactory.class.getClassLoader(), Native2AsciiAdapter.class);
  }
  
  private static final boolean shouldUseKaffe()
  {
    return (JavaEnvUtils.isKaffe()) || (JavaEnvUtils.isClasspathBased());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapterFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */