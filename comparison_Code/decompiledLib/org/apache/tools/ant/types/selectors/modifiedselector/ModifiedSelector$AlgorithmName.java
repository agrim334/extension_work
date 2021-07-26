package org.apache.tools.ant.types.selectors.modifiedselector;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class ModifiedSelector$AlgorithmName
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "hashvalue", "digest", "checksum", "lastmodified" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector.AlgorithmName
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */