package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;

final class PatternSet$InvertedPatternSet
  extends PatternSet
{
  private PatternSet$InvertedPatternSet(PatternSet p)
  {
    setProject(p.getProject());
    addConfiguredPatternset(p);
  }
  
  public String[] getIncludePatterns(Project p)
  {
    return super.getExcludePatterns(p);
  }
  
  public String[] getExcludePatterns(Project p)
  {
    return super.getIncludePatterns(p);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.PatternSet.InvertedPatternSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */