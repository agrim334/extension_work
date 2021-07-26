package org.apache.tools.ant.util;

import java.io.File;

public class UnPackageNameMapper
  extends GlobPatternMapper
{
  protected String extractVariablePart(String name)
  {
    String var = name.substring(prefixLength, name
      .length() - postfixLength);
    return var.replace('.', File.separatorChar);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.UnPackageNameMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */