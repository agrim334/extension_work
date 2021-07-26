package org.apache.tools.ant.taskdefs.condition;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Available;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.UpToDate;

public abstract class ConditionBase
  extends ProjectComponent
{
  private String taskName = "condition";
  private List<Condition> conditions = new Vector();
  
  protected ConditionBase()
  {
    taskName = "component";
  }
  
  protected ConditionBase(String taskName)
  {
    this.taskName = taskName;
  }
  
  protected int countConditions()
  {
    return conditions.size();
  }
  
  protected final Enumeration<Condition> getConditions()
  {
    return Collections.enumeration(conditions);
  }
  
  public void setTaskName(String name)
  {
    taskName = name;
  }
  
  public String getTaskName()
  {
    return taskName;
  }
  
  public void addAvailable(Available a)
  {
    conditions.add(a);
  }
  
  public void addChecksum(Checksum c)
  {
    conditions.add(c);
  }
  
  public void addUptodate(UpToDate u)
  {
    conditions.add(u);
  }
  
  public void addNot(Not n)
  {
    conditions.add(n);
  }
  
  public void addAnd(And a)
  {
    conditions.add(a);
  }
  
  public void addOr(Or o)
  {
    conditions.add(o);
  }
  
  public void addEquals(Equals e)
  {
    conditions.add(e);
  }
  
  public void addOs(Os o)
  {
    conditions.add(o);
  }
  
  public void addIsSet(IsSet i)
  {
    conditions.add(i);
  }
  
  public void addHttp(Http h)
  {
    conditions.add(h);
  }
  
  public void addSocket(Socket s)
  {
    conditions.add(s);
  }
  
  public void addFilesMatch(FilesMatch test)
  {
    conditions.add(test);
  }
  
  public void addContains(Contains test)
  {
    conditions.add(test);
  }
  
  public void addIsTrue(IsTrue test)
  {
    conditions.add(test);
  }
  
  public void addIsFalse(IsFalse test)
  {
    conditions.add(test);
  }
  
  public void addIsReference(IsReference i)
  {
    conditions.add(i);
  }
  
  public void addIsFileSelected(IsFileSelected test)
  {
    conditions.add(test);
  }
  
  public void add(Condition c)
  {
    conditions.add(c);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.ConditionBase
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */