package org.apache.tools.ant.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;

public class ScriptFixBSFPath
{
  private static final String UTIL_OPTIONAL_PACKAGE = "org.apache.tools.ant.util.optional";
  private static final String BSF_PACKAGE = "org.apache.bsf";
  private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
  private static final String BSF_SCRIPT_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
  private static final String[] BSF_LANGUAGES = { "js", "org.mozilla.javascript.Scriptable", "javascript", "org.mozilla.javascript.Scriptable", "jacl", "tcl.lang.Interp", "netrexx", "netrexx.lang.Rexx", "nrx", "netrexx.lang.Rexx", "jython", "org.python.core.Py", "py", "org.python.core.Py", "xslt", "org.apache.xpath.objects.XObject" };
  private static final Map<String, String> BSF_LANGUAGE_MAP = new HashMap();
  
  static
  {
    for (int i = 0; i < BSF_LANGUAGES.length; i += 2) {
      BSF_LANGUAGE_MAP.put(BSF_LANGUAGES[i], BSF_LANGUAGES[(i + 1)]);
    }
  }
  
  private File getClassSource(ClassLoader loader, String className)
  {
    return LoaderUtils.getResourceSource(loader, 
    
      LoaderUtils.classNameToResource(className));
  }
  
  private File getClassSource(String className)
  {
    return getClassSource(getClass().getClassLoader(), className);
  }
  
  public void fixClassLoader(ClassLoader loader, String language)
  {
    if ((loader == getClass().getClassLoader()) || (!(loader instanceof AntClassLoader))) {
      return;
    }
    ClassLoader myLoader = getClass().getClassLoader();
    AntClassLoader fixLoader = (AntClassLoader)loader;
    
    File bsfSource = getClassSource("org.apache.bsf.BSFManager");
    
    boolean needMoveRunner = bsfSource == null;
    
    String languageClassName = (String)BSF_LANGUAGE_MAP.get(language);
    if ((bsfSource != null) && (languageClassName != null)) {}
    boolean needMoveBsf = (!LoaderUtils.classExists(myLoader, languageClassName)) && (LoaderUtils.classExists(loader, languageClassName));
    
    needMoveRunner = (needMoveRunner) || (needMoveBsf);
    if (bsfSource == null) {
      bsfSource = getClassSource(loader, "org.apache.bsf.BSFManager");
    }
    if (bsfSource == null) {
      throw new BuildException("Unable to find BSF classes for scripting");
    }
    if (needMoveBsf)
    {
      fixLoader.addPathComponent(bsfSource);
      fixLoader.addLoaderPackageRoot("org.apache.bsf");
    }
    if (needMoveRunner)
    {
      fixLoader.addPathComponent(
        LoaderUtils.getResourceSource(fixLoader, 
        
        LoaderUtils.classNameToResource("org.apache.tools.ant.util.optional.ScriptRunner")));
      fixLoader.addLoaderPackageRoot("org.apache.tools.ant.util.optional");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ScriptFixBSFPath
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */