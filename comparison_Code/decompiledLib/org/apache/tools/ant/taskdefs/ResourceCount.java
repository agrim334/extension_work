package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;

public class ResourceCount
  extends Task
  implements Condition
{
  private static final String ONE_NESTED_MESSAGE = "ResourceCount can count resources from exactly one nested ResourceCollection.";
  private static final String COUNT_REQUIRED = "Use of the ResourceCount condition requires that the count attribute be set.";
  private ResourceCollection rc;
  private Comparison when = Comparison.EQUAL;
  private Integer count;
  private String property;
  
  public void add(ResourceCollection r)
  {
    if (rc != null) {
      throw new BuildException("ResourceCount can count resources from exactly one nested ResourceCollection.");
    }
    rc = r;
  }
  
  public void setRefid(Reference r)
  {
    Object o = r.getReferencedObject();
    if (!(o instanceof ResourceCollection)) {
      throw new BuildException("%s doesn't denote a ResourceCollection", new Object[] {r.getRefId() });
    }
    add((ResourceCollection)o);
  }
  
  public void execute()
  {
    if (rc == null) {
      throw new BuildException("ResourceCount can count resources from exactly one nested ResourceCollection.");
    }
    if (property == null) {
      log("resource count = " + rc.size());
    } else {
      getProject().setNewProperty(property, Integer.toString(rc.size()));
    }
  }
  
  public boolean eval()
  {
    if (rc == null) {
      throw new BuildException("ResourceCount can count resources from exactly one nested ResourceCollection.");
    }
    if (count == null) {
      throw new BuildException("Use of the ResourceCount condition requires that the count attribute be set.");
    }
    return when.evaluate(Integer.valueOf(rc.size()).compareTo(count));
  }
  
  public void setCount(int c)
  {
    count = Integer.valueOf(c);
  }
  
  public void setWhen(Comparison c)
  {
    when = c;
  }
  
  public void setProperty(String p)
  {
    property = p;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ResourceCount
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */