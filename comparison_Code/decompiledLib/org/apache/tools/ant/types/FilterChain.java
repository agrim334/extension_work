package org.apache.tools.ant.types;

import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.filters.ClassConstants;
import org.apache.tools.ant.filters.EscapeUnicode;
import org.apache.tools.ant.filters.ExpandProperties;
import org.apache.tools.ant.filters.HeadFilter;
import org.apache.tools.ant.filters.LineContains;
import org.apache.tools.ant.filters.LineContainsRegExp;
import org.apache.tools.ant.filters.PrefixLines;
import org.apache.tools.ant.filters.ReplaceTokens;
import org.apache.tools.ant.filters.StripJavaComments;
import org.apache.tools.ant.filters.StripLineBreaks;
import org.apache.tools.ant.filters.StripLineComments;
import org.apache.tools.ant.filters.SuffixLines;
import org.apache.tools.ant.filters.TabsToSpaces;
import org.apache.tools.ant.filters.TailFilter;
import org.apache.tools.ant.filters.TokenFilter;
import org.apache.tools.ant.filters.TokenFilter.ContainsRegex;
import org.apache.tools.ant.filters.TokenFilter.DeleteCharacters;
import org.apache.tools.ant.filters.TokenFilter.IgnoreBlank;
import org.apache.tools.ant.filters.TokenFilter.ReplaceRegex;
import org.apache.tools.ant.filters.TokenFilter.ReplaceString;
import org.apache.tools.ant.filters.TokenFilter.Trim;

public class FilterChain
  extends DataType
{
  private Vector<Object> filterReaders = new Vector();
  
  public void addFilterReader(AntFilterReader filterReader)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filterReader);
  }
  
  public Vector<Object> getFilterReaders()
  {
    if (isReference()) {
      return getRef().getFilterReaders();
    }
    dieOnCircularReference();
    return filterReaders;
  }
  
  public void addClassConstants(ClassConstants classConstants)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(classConstants);
  }
  
  public void addExpandProperties(ExpandProperties expandProperties)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(expandProperties);
  }
  
  public void addHeadFilter(HeadFilter headFilter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(headFilter);
  }
  
  public void addLineContains(LineContains lineContains)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(lineContains);
  }
  
  public void addLineContainsRegExp(LineContainsRegExp lineContainsRegExp)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(lineContainsRegExp);
  }
  
  public void addPrefixLines(PrefixLines prefixLines)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(prefixLines);
  }
  
  public void addSuffixLines(SuffixLines suffixLines)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(suffixLines);
  }
  
  public void addReplaceTokens(ReplaceTokens replaceTokens)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(replaceTokens);
  }
  
  public void addStripJavaComments(StripJavaComments stripJavaComments)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(stripJavaComments);
  }
  
  public void addStripLineBreaks(StripLineBreaks stripLineBreaks)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(stripLineBreaks);
  }
  
  public void addStripLineComments(StripLineComments stripLineComments)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(stripLineComments);
  }
  
  public void addTabsToSpaces(TabsToSpaces tabsToSpaces)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(tabsToSpaces);
  }
  
  public void addTailFilter(TailFilter tailFilter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(tailFilter);
  }
  
  public void addEscapeUnicode(EscapeUnicode escapeUnicode)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(escapeUnicode);
  }
  
  public void addTokenFilter(TokenFilter tokenFilter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(tokenFilter);
  }
  
  public void addDeleteCharacters(TokenFilter.DeleteCharacters filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
  }
  
  public void addContainsRegex(TokenFilter.ContainsRegex filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
  }
  
  public void addReplaceRegex(TokenFilter.ReplaceRegex filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
  }
  
  public void addTrim(TokenFilter.Trim filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
  }
  
  public void addReplaceString(TokenFilter.ReplaceString filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
  }
  
  public void addIgnoreBlank(TokenFilter.IgnoreBlank filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
  }
  
  public void setRefid(Reference r)
    throws BuildException
  {
    if (!filterReaders.isEmpty()) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public void add(ChainableReader filter)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    setChecked(false);
    filterReaders.addElement(filter);
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
      for (Object o : filterReaders) {
        if ((o instanceof DataType)) {
          pushAndInvokeCircularReferenceCheck((DataType)o, stk, p);
        }
      }
      setChecked(true);
    }
  }
  
  private FilterChain getRef()
  {
    return (FilterChain)getCheckedRef(FilterChain.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.FilterChain
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */