package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.PatternSet.NameEntry;

public class Chmod
  extends ExecuteOn
{
  private FileSet defaultSet = new FileSet();
  private boolean defaultSetDefined = false;
  private boolean havePerm = false;
  
  public Chmod()
  {
    super.setExecutable("chmod");
    super.setParallel(true);
    super.setSkipEmptyFilesets(true);
  }
  
  public void setProject(Project project)
  {
    super.setProject(project);
    defaultSet.setProject(project);
  }
  
  public void setFile(File src)
  {
    FileSet fs = new FileSet();
    fs.setFile(src);
    addFileset(fs);
  }
  
  public void setDir(File src)
  {
    defaultSet.setDir(src);
  }
  
  public void setPerm(String perm)
  {
    createArg().setValue(perm);
    havePerm = true;
  }
  
  public PatternSet.NameEntry createInclude()
  {
    defaultSetDefined = true;
    return defaultSet.createInclude();
  }
  
  public PatternSet.NameEntry createExclude()
  {
    defaultSetDefined = true;
    return defaultSet.createExclude();
  }
  
  public PatternSet createPatternSet()
  {
    defaultSetDefined = true;
    return defaultSet.createPatternSet();
  }
  
  public void setIncludes(String includes)
  {
    defaultSetDefined = true;
    defaultSet.setIncludes(includes);
  }
  
  public void setExcludes(String excludes)
  {
    defaultSetDefined = true;
    defaultSet.setExcludes(excludes);
  }
  
  public void setDefaultexcludes(boolean useDefaultExcludes)
  {
    defaultSetDefined = true;
    defaultSet.setDefaultexcludes(useDefaultExcludes);
  }
  
  protected void checkConfiguration()
  {
    if (!havePerm) {
      throw new BuildException("Required attribute perm not set in chmod", getLocation());
    }
    if ((defaultSetDefined) && (defaultSet.getDir(getProject()) != null)) {
      addFileset(defaultSet);
    }
    super.checkConfiguration();
  }
  
  /* Error */
  public void execute()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 16	org/apache/tools/ant/taskdefs/Chmod:defaultSetDefined	Z
    //   4: ifne +17 -> 21
    //   7: aload_0
    //   8: getfield 10	org/apache/tools/ant/taskdefs/Chmod:defaultSet	Lorg/apache/tools/ant/types/FileSet;
    //   11: aload_0
    //   12: invokevirtual 92	org/apache/tools/ant/taskdefs/Chmod:getProject	()Lorg/apache/tools/ant/Project;
    //   15: invokevirtual 96	org/apache/tools/ant/types/FileSet:getDir	(Lorg/apache/tools/ant/Project;)Ljava/io/File;
    //   18: ifnonnull +82 -> 100
    //   21: aload_0
    //   22: invokespecial 103	org/apache/tools/ant/taskdefs/ExecuteOn:execute	()V
    //   25: aload_0
    //   26: getfield 16	org/apache/tools/ant/taskdefs/Chmod:defaultSetDefined	Z
    //   29: ifeq +68 -> 97
    //   32: aload_0
    //   33: getfield 10	org/apache/tools/ant/taskdefs/Chmod:defaultSet	Lorg/apache/tools/ant/types/FileSet;
    //   36: aload_0
    //   37: invokevirtual 92	org/apache/tools/ant/taskdefs/Chmod:getProject	()Lorg/apache/tools/ant/Project;
    //   40: invokevirtual 96	org/apache/tools/ant/types/FileSet:getDir	(Lorg/apache/tools/ant/Project;)Ljava/io/File;
    //   43: ifnull +54 -> 97
    //   46: aload_0
    //   47: getfield 106	org/apache/tools/ant/taskdefs/Chmod:filesets	Ljava/util/Vector;
    //   50: aload_0
    //   51: getfield 10	org/apache/tools/ant/taskdefs/Chmod:defaultSet	Lorg/apache/tools/ant/types/FileSet;
    //   54: invokevirtual 110	java/util/Vector:removeElement	(Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto +39 -> 97
    //   61: astore_1
    //   62: aload_0
    //   63: getfield 16	org/apache/tools/ant/taskdefs/Chmod:defaultSetDefined	Z
    //   66: ifeq +29 -> 95
    //   69: aload_0
    //   70: getfield 10	org/apache/tools/ant/taskdefs/Chmod:defaultSet	Lorg/apache/tools/ant/types/FileSet;
    //   73: aload_0
    //   74: invokevirtual 92	org/apache/tools/ant/taskdefs/Chmod:getProject	()Lorg/apache/tools/ant/Project;
    //   77: invokevirtual 96	org/apache/tools/ant/types/FileSet:getDir	(Lorg/apache/tools/ant/Project;)Ljava/io/File;
    //   80: ifnull +15 -> 95
    //   83: aload_0
    //   84: getfield 106	org/apache/tools/ant/taskdefs/Chmod:filesets	Ljava/util/Vector;
    //   87: aload_0
    //   88: getfield 10	org/apache/tools/ant/taskdefs/Chmod:defaultSet	Lorg/apache/tools/ant/types/FileSet;
    //   91: invokevirtual 110	java/util/Vector:removeElement	(Ljava/lang/Object;)Z
    //   94: pop
    //   95: aload_1
    //   96: athrow
    //   97: goto +109 -> 206
    //   100: aload_0
    //   101: invokevirtual 116	org/apache/tools/ant/taskdefs/Chmod:isValidOs	()Z
    //   104: ifeq +102 -> 206
    //   107: aload_0
    //   108: invokevirtual 120	org/apache/tools/ant/taskdefs/Chmod:prepareExec	()Lorg/apache/tools/ant/taskdefs/Execute;
    //   111: astore_1
    //   112: aload_0
    //   113: getfield 124	org/apache/tools/ant/taskdefs/Chmod:cmdl	Lorg/apache/tools/ant/types/Commandline;
    //   116: invokevirtual 128	org/apache/tools/ant/types/Commandline:clone	()Ljava/lang/Object;
    //   119: checkcast 129	org/apache/tools/ant/types/Commandline
    //   122: astore_2
    //   123: aload_2
    //   124: invokevirtual 134	org/apache/tools/ant/types/Commandline:createArgument	()Lorg/apache/tools/ant/types/Commandline$Argument;
    //   127: aload_0
    //   128: getfield 10	org/apache/tools/ant/taskdefs/Chmod:defaultSet	Lorg/apache/tools/ant/types/FileSet;
    //   131: aload_0
    //   132: invokevirtual 92	org/apache/tools/ant/taskdefs/Chmod:getProject	()Lorg/apache/tools/ant/Project;
    //   135: invokevirtual 96	org/apache/tools/ant/types/FileSet:getDir	(Lorg/apache/tools/ant/Project;)Ljava/io/File;
    //   138: invokevirtual 137	java/io/File:getPath	()Ljava/lang/String;
    //   141: invokevirtual 56	org/apache/tools/ant/types/Commandline$Argument:setValue	(Ljava/lang/String;)V
    //   144: aload_1
    //   145: aload_2
    //   146: invokevirtual 143	org/apache/tools/ant/types/Commandline:getCommandline	()[Ljava/lang/String;
    //   149: invokevirtual 147	org/apache/tools/ant/taskdefs/Execute:setCommandline	([Ljava/lang/String;)V
    //   152: aload_0
    //   153: aload_1
    //   154: invokevirtual 153	org/apache/tools/ant/taskdefs/Chmod:runExecute	(Lorg/apache/tools/ant/taskdefs/Execute;)V
    //   157: aload_0
    //   158: invokevirtual 157	org/apache/tools/ant/taskdefs/Chmod:logFlush	()V
    //   161: goto +45 -> 206
    //   164: astore_3
    //   165: new 81	org/apache/tools/ant/BuildException
    //   168: dup
    //   169: new 162	java/lang/StringBuilder
    //   172: dup
    //   173: invokespecial 164	java/lang/StringBuilder:<init>	()V
    //   176: ldc -91
    //   178: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   181: aload_3
    //   182: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: aload_3
    //   189: aload_0
    //   190: invokevirtual 85	org/apache/tools/ant/taskdefs/Chmod:getLocation	()Lorg/apache/tools/ant/Location;
    //   193: invokespecial 177	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;Lorg/apache/tools/ant/Location;)V
    //   196: athrow
    //   197: astore 4
    //   199: aload_0
    //   200: invokevirtual 157	org/apache/tools/ant/taskdefs/Chmod:logFlush	()V
    //   203: aload 4
    //   205: athrow
    //   206: return
    // Line number table:
    //   Java source line #184	-> byte code offset #0
    //   Java source line #186	-> byte code offset #21
    //   Java source line #188	-> byte code offset #25
    //   Java source line #189	-> byte code offset #46
    //   Java source line #188	-> byte code offset #61
    //   Java source line #189	-> byte code offset #83
    //   Java source line #191	-> byte code offset #95
    //   Java source line #192	-> byte code offset #100
    //   Java source line #194	-> byte code offset #107
    //   Java source line #195	-> byte code offset #112
    //   Java source line #196	-> byte code offset #123
    //   Java source line #197	-> byte code offset #138
    //   Java source line #196	-> byte code offset #141
    //   Java source line #199	-> byte code offset #144
    //   Java source line #200	-> byte code offset #152
    //   Java source line #205	-> byte code offset #157
    //   Java source line #206	-> byte code offset #161
    //   Java source line #201	-> byte code offset #164
    //   Java source line #202	-> byte code offset #165
    //   Java source line #205	-> byte code offset #197
    //   Java source line #206	-> byte code offset #203
    //   Java source line #208	-> byte code offset #206
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	207	0	this	Chmod
    //   61	35	1	localObject1	Object
    //   111	43	1	execute	Execute
    //   122	24	2	cloned	Commandline
    //   164	25	3	e	java.io.IOException
    //   197	7	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   21	25	61	finally
    //   144	157	164	java/io/IOException
    //   144	157	197	finally
    //   164	199	197	finally
  }
  
  public void setExecutable(String e)
  {
    throw new BuildException(getTaskType() + " doesn't support the executable attribute", getLocation());
  }
  
  public void setCommand(Commandline cmdl)
  {
    throw new BuildException(getTaskType() + " doesn't support the command attribute", getLocation());
  }
  
  public void setSkipEmptyFilesets(boolean skip)
  {
    throw new BuildException(getTaskType() + " doesn't support the skipemptyfileset attribute", getLocation());
  }
  
  public void setAddsourcefile(boolean b)
  {
    throw new BuildException(getTaskType() + " doesn't support the addsourcefile attribute", getLocation());
  }
  
  protected boolean isValidOs()
  {
    return (getOs() == null) && (getOsFamily() == null) ? 
      Os.isFamily("unix") : super.isValidOs();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Chmod
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */