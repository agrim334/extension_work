package org.apache.tools.ant.types;

 enum Quantifier$Predicate$4
{
  Quantifier$Predicate$4(String primaryName, String... additionalNames)
  {
    super(paramString, paramInt, primaryName, additionalNames, null);
  }
  
  boolean eval(int t, int f)
  {
    return t > f;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Quantifier.Predicate.4
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */