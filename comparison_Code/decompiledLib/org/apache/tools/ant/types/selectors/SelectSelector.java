package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Enumeration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

public class SelectSelector
  extends BaseSelectorContainer
{
  private Object ifCondition;
  private Object unlessCondition;
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    if (hasSelectors())
    {
      buf.append("{select");
      if (ifCondition != null)
      {
        buf.append(" if: ");
        buf.append(ifCondition);
      }
      if (unlessCondition != null)
      {
        buf.append(" unless: ");
        buf.append(unlessCondition);
      }
      buf.append(" ");
      buf.append(super.toString());
      buf.append("}");
    }
    return buf.toString();
  }
  
  private SelectSelector getRef()
  {
    return (SelectSelector)getCheckedRef(SelectSelector.class);
  }
  
  public boolean hasSelectors()
  {
    if (isReference()) {
      return getRef().hasSelectors();
    }
    return super.hasSelectors();
  }
  
  public int selectorCount()
  {
    if (isReference()) {
      return getRef().selectorCount();
    }
    return super.selectorCount();
  }
  
  public FileSelector[] getSelectors(Project p)
  {
    if (isReference()) {
      return getRef().getSelectors(p);
    }
    return super.getSelectors(p);
  }
  
  public Enumeration<FileSelector> selectorElements()
  {
    if (isReference()) {
      return getRef().selectorElements();
    }
    return super.selectorElements();
  }
  
  public void appendSelector(FileSelector selector)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    super.appendSelector(selector);
  }
  
  public void verifySettings()
  {
    int cnt = selectorCount();
    if ((cnt < 0) || (cnt > 1)) {
      setError("Only one selector is allowed within the <selector> tag");
    }
  }
  
  public boolean passesConditions()
  {
    PropertyHelper ph = PropertyHelper.getPropertyHelper(getProject());
    return (ph.testIfCondition(ifCondition)) && 
      (ph.testUnlessCondition(unlessCondition));
  }
  
  public void setIf(Object ifProperty)
  {
    ifCondition = ifProperty;
  }
  
  public void setIf(String ifProperty)
  {
    setIf(ifProperty);
  }
  
  public void setUnless(Object unlessProperty)
  {
    unlessCondition = unlessProperty;
  }
  
  public void setUnless(String unlessProperty)
  {
    setUnless(unlessProperty);
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    if (!passesConditions()) {
      return false;
    }
    Enumeration<FileSelector> e = selectorElements();
    return (!e.hasMoreElements()) || (((FileSelector)e.nextElement()).isSelected(basedir, filename, file));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.SelectSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */