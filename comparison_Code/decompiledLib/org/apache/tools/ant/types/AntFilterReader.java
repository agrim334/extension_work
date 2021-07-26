package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public final class AntFilterReader
  extends DataType
{
  private String className;
  private final List<Parameter> parameters = new ArrayList();
  private Path classpath;
  
  public void setClassName(String className)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.className = className;
  }
  
  public String getClassName()
  {
    if (isReference()) {
      return getRef().getClassName();
    }
    dieOnCircularReference();
    return className;
  }
  
  public void addParam(Parameter param)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    parameters.add(param);
  }
  
  public void setClasspath(Path classpath)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if (this.classpath == null) {
      this.classpath = classpath;
    } else {
      this.classpath.append(classpath);
    }
    setChecked(false);
  }
  
  public Path createClasspath()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    setChecked(false);
    return classpath.createPath();
  }
  
  public Path getClasspath()
  {
    if (isReference()) {
      getRef().getClasspath();
    }
    dieOnCircularReference();
    return classpath;
  }
  
  public void setClasspathRef(Reference r)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    createClasspath().setRefid(r);
  }
  
  public Parameter[] getParams()
  {
    if (isReference()) {
      getRef().getParams();
    }
    dieOnCircularReference();
    return (Parameter[])parameters.toArray(new Parameter[parameters.size()]);
  }
  
  public void setRefid(Reference r)
    throws BuildException
  {
    if ((!parameters.isEmpty()) || (className != null) || (classpath != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p)
    throws BuildException
  {
    if (isChecked()) {
      return;
    }
    if (isReference())
    {
      super.dieOnCircularReference(stk, p);
    }
    else
    {
      if (classpath != null) {
        pushAndInvokeCircularReferenceCheck(classpath, stk, p);
      }
      setChecked(true);
    }
  }
  
  private AntFilterReader getRef()
  {
    return (AntFilterReader)getCheckedRef(AntFilterReader.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.AntFilterReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */