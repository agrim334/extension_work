package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public abstract interface ExtensionResolver
{
  public abstract File resolve(Extension paramExtension, Project paramProject)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */