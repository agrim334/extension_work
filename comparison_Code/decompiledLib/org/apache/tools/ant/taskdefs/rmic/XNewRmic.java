package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.types.Commandline;

public class XNewRmic
  extends ForkingSunRmic
{
  public static final String COMPILER_NAME = "xnew";
  
  protected Commandline setupRmicCommand()
  {
    String[] options = { "-Xnew" };
    
    return super.setupRmicCommand(options);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.XNewRmic
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */