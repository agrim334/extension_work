package org.apache.tools.ant.types.selectors;

import java.util.Enumeration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

public abstract interface SelectorContainer
{
  public abstract boolean hasSelectors();
  
  public abstract int selectorCount();
  
  public abstract FileSelector[] getSelectors(Project paramProject);
  
  public abstract Enumeration<FileSelector> selectorElements();
  
  public abstract void appendSelector(FileSelector paramFileSelector);
  
  public abstract void addSelector(SelectSelector paramSelectSelector);
  
  public abstract void addAnd(AndSelector paramAndSelector);
  
  public abstract void addOr(OrSelector paramOrSelector);
  
  public abstract void addNot(NotSelector paramNotSelector);
  
  public abstract void addNone(NoneSelector paramNoneSelector);
  
  public abstract void addMajority(MajoritySelector paramMajoritySelector);
  
  public abstract void addDate(DateSelector paramDateSelector);
  
  public abstract void addSize(SizeSelector paramSizeSelector);
  
  public abstract void addFilename(FilenameSelector paramFilenameSelector);
  
  public abstract void addCustom(ExtendSelector paramExtendSelector);
  
  public abstract void addContains(ContainsSelector paramContainsSelector);
  
  public abstract void addPresent(PresentSelector paramPresentSelector);
  
  public abstract void addDepth(DepthSelector paramDepthSelector);
  
  public abstract void addDepend(DependSelector paramDependSelector);
  
  public abstract void addContainsRegexp(ContainsRegexpSelector paramContainsRegexpSelector);
  
  public abstract void addType(TypeSelector paramTypeSelector);
  
  public abstract void addDifferent(DifferentSelector paramDifferentSelector);
  
  public abstract void addModified(ModifiedSelector paramModifiedSelector);
  
  public abstract void add(FileSelector paramFileSelector);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.SelectorContainer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */