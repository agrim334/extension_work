package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class ScriptRunnerCreator
{
  private static final String AUTO = "auto";
  private static final String OATAU = "org.apache.tools.ant.util";
  private static final String UTIL_OPT = "org.apache.tools.ant.util.optional";
  private static final String BSF = "bsf";
  private static final String BSF_PACK = "org.apache.bsf";
  private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
  private static final String BSF_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
  private static final String JAVAX = "javax";
  private static final String JAVAX_MANAGER = "javax.script.ScriptEngineManager";
  private static final String JAVAX_RUNNER = "org.apache.tools.ant.util.optional.JavaxScriptRunner";
  private Project project;
  private String manager;
  private String language;
  private ClassLoader scriptLoader = null;
  
  public ScriptRunnerCreator(Project project)
  {
    this.project = project;
  }
  
  public synchronized ScriptRunnerBase createRunner(String manager, String language, ClassLoader classLoader)
  {
    this.manager = manager;
    this.language = language;
    scriptLoader = classLoader;
    if (language == null) {
      throw new BuildException("script language must be specified");
    }
    if ((!manager.equals("auto")) && (!manager.equals("javax")) && (!manager.equals("bsf"))) {
      throw new BuildException("Unsupported language prefix " + manager);
    }
    ScriptRunnerBase ret = null;
    ret = createRunner("bsf", "org.apache.bsf.BSFManager", "org.apache.tools.ant.util.optional.ScriptRunner");
    if (ret == null) {
      ret = createRunner("javax", "javax.script.ScriptEngineManager", "org.apache.tools.ant.util.optional.JavaxScriptRunner");
    }
    if (ret != null) {
      return ret;
    }
    if ("javax".equals(manager)) {
      throw new BuildException("Unable to load the script engine manager (javax.script.ScriptEngineManager)");
    }
    if ("bsf".equals(manager)) {
      throw new BuildException("Unable to load the BSF script engine manager (org.apache.bsf.BSFManager)");
    }
    throw new BuildException("Unable to load a script engine manager (org.apache.bsf.BSFManager or javax.script.ScriptEngineManager)");
  }
  
  private ScriptRunnerBase createRunner(String checkManager, String managerClass, String runnerClass)
  {
    ScriptRunnerBase runner = null;
    if ((!manager.equals("auto")) && (!manager.equals(checkManager))) {
      return null;
    }
    if (managerClass.equals("org.apache.bsf.BSFManager"))
    {
      if (scriptLoader.getResource(LoaderUtils.classNameToResource(managerClass)) == null) {
        return null;
      }
      new ScriptFixBSFPath().fixClassLoader(scriptLoader, language);
    }
    else
    {
      try
      {
        Class.forName(managerClass, true, scriptLoader);
      }
      catch (Exception ex)
      {
        return null;
      }
    }
    try
    {
      runner = (ScriptRunnerBase)Class.forName(runnerClass, true, scriptLoader).newInstance();
      runner.setProject(project);
    }
    catch (Exception ex)
    {
      throw ReflectUtil.toBuildException(ex);
    }
    runner.setLanguage(language);
    runner.setScriptClassLoader(scriptLoader);
    return runner;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ScriptRunnerCreator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */