package org.apache.tools.ant.types;

 enum Quantifier$Predicate$2
{
  Quantifier$Predicate$2(String primaryName, String... additionalNames)
  {
    super(paramString, paramInt, primaryName, additionalNames, null);
  }
  
  boolean eval(int t, int f)
  {
    return t > 0;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Quantifier.Predicate.2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */