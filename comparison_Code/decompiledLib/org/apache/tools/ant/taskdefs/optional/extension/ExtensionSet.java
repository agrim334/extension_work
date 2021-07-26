package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;

public class ExtensionSet
  extends DataType
{
  private final List<ExtensionAdapter> extensions = new ArrayList();
  private final List<FileSet> extensionsFilesets = new ArrayList();
  
  public void addExtension(ExtensionAdapter extensionAdapter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    extensions.add(extensionAdapter);
  }
  
  public void addLibfileset(LibFileSet fileSet)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    extensionsFilesets.add(fileSet);
  }
  
  public void addFileset(FileSet fileSet)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    extensionsFilesets.add(fileSet);
  }
  
  public Extension[] toExtensions(Project proj)
    throws BuildException
  {
    if (isReference()) {
      return getRef().toExtensions(proj);
    }
    dieOnCircularReference();
    List<Extension> extensionsList = ExtensionUtil.toExtensions(extensions);
    ExtensionUtil.extractExtensions(proj, extensionsList, extensionsFilesets);
    return (Extension[])extensionsList.toArray(new Extension[extensionsList.size()]);
  }
  
  public void setRefid(Reference reference)
    throws BuildException
  {
    if ((!extensions.isEmpty()) || (!extensionsFilesets.isEmpty())) {
      throw tooManyAttributes();
    }
    super.setRefid(reference);
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
      for (ExtensionAdapter extensionAdapter : extensions) {
        pushAndInvokeCircularReferenceCheck(extensionAdapter, stk, p);
      }
      for (FileSet fileSet : extensionsFilesets) {
        pushAndInvokeCircularReferenceCheck(fileSet, stk, p);
      }
      setChecked(true);
    }
  }
  
  private ExtensionSet getRef()
  {
    return (ExtensionSet)getCheckedRef(ExtensionSet.class);
  }
  
  public String toString()
  {
    return "ExtensionSet" + Arrays.asList(toExtensions(getProject()));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.ExtensionSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */