package org.apache.tools.ant.types;

 enum Quantifier$Predicate$1
{
  Quantifier$Predicate$1(String primaryName, String... additionalNames)
  {
    super(paramString, paramInt, primaryName, additionalNames, null);
  }
  
  boolean eval(int t, int f)
  {
    return f == 0;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Quantifier.Predicate.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */