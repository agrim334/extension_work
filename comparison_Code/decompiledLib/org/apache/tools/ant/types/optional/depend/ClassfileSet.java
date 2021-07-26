package org.apache.tools.ant.types.optional.depend;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.StringUtils;

public class ClassfileSet
  extends FileSet
{
  private List<String> rootClasses = new ArrayList();
  private List<FileSet> rootFileSets = new ArrayList();
  public ClassfileSet() {}
  
  public static class ClassRoot
  {
    private String rootClass;
    
    public void setClassname(String name)
    {
      rootClass = name;
    }
    
    public String getClassname()
    {
      return rootClass;
    }
  }
  
  protected ClassfileSet(ClassfileSet s)
  {
    super(s);
    rootClasses.addAll(rootClasses);
  }
  
  public void addRootFileset(FileSet rootFileSet)
  {
    rootFileSets.add(rootFileSet);
    setChecked(false);
  }
  
  public void setRootClass(String rootClass)
  {
    rootClasses.add(rootClass);
  }
  
  public DirectoryScanner getDirectoryScanner(Project p)
  {
    if (isReference()) {
      return getRef(p).getDirectoryScanner(p);
    }
    dieOnCircularReference(p);
    DirectoryScanner parentScanner = super.getDirectoryScanner(p);
    DependScanner scanner = new DependScanner(parentScanner);
    Vector<String> allRootClasses = new Vector(rootClasses);
    for (FileSet additionalRootSet : rootFileSets)
    {
      DirectoryScanner additionalScanner = additionalRootSet.getDirectoryScanner(p);
      for (String file : additionalScanner.getIncludedFiles()) {
        if (file.endsWith(".class"))
        {
          String classFilePath = StringUtils.removeSuffix(file, ".class");
          
          String className = classFilePath.replace('/', '.').replace('\\', '.');
          allRootClasses.addElement(className);
        }
      }
      scanner.addBasedir(additionalRootSet.getDir(p));
    }
    scanner.setBasedir(getDir(p));
    scanner.setRootClasses(allRootClasses);
    scanner.scan();
    return scanner;
  }
  
  public void addConfiguredRoot(ClassRoot root)
  {
    rootClasses.add(root.getClassname());
  }
  
  public Object clone()
  {
    return new ClassfileSet(isReference() ? getRef() : this);
  }
  
  protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p)
  {
    if (isChecked()) {
      return;
    }
    super.dieOnCircularReference(stk, p);
    if (!isReference())
    {
      for (FileSet additionalRootSet : rootFileSets) {
        pushAndInvokeCircularReferenceCheck(additionalRootSet, stk, p);
      }
      setChecked(true);
    }
  }
  
  private ClassfileSet getRef()
  {
    return (ClassfileSet)getCheckedRef(ClassfileSet.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.optional.depend.ClassfileSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */