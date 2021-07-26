package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;

public class Jasper41Mangler
  implements JspMangler
{
  public String mapJspToJavaName(File jspFile)
  {
    String jspUri = jspFile.getAbsolutePath();
    int start = jspUri.lastIndexOf(File.separatorChar) + 1;
    StringBuilder modifiedClassName = new StringBuilder(jspUri.length() - start);
    if ((!Character.isJavaIdentifierStart(jspUri.charAt(start))) || 
      (jspUri.charAt(start) == '_')) {
      modifiedClassName.append('_');
    }
    for (char ch : jspUri.substring(start).toCharArray()) {
      if (Character.isJavaIdentifierPart(ch)) {
        modifiedClassName.append(ch);
      } else if (ch == '.') {
        modifiedClassName.append('_');
      } else {
        modifiedClassName.append(mangleChar(ch));
      }
    }
    return modifiedClassName.toString();
  }
  
  private static String mangleChar(char ch)
  {
    String s = Integer.toHexString(ch);
    int nzeros = 5 - s.length();
    char[] result = new char[6];
    result[0] = '_';
    for (int i = 1; i <= nzeros; i++) {
      result[i] = '0';
    }
    int i = nzeros + 1;
    for (int j = 0; i < 6; j++)
    {
      result[i] = s.charAt(j);i++;
    }
    return new String(result);
  }
  
  public String mapPath(String path)
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.Jasper41Mangler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */