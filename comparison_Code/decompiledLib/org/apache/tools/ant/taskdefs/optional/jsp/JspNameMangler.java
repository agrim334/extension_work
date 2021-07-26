package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import org.apache.tools.ant.util.StringUtils;

public class JspNameMangler
  implements JspMangler
{
  public static final String[] keywords = { "assert", "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };
  
  public String mapJspToJavaName(File jspFile)
  {
    return mapJspToBaseName(jspFile) + ".java";
  }
  
  private String mapJspToBaseName(File jspFile)
  {
    String className = stripExtension(jspFile);
    for (keyword : keywords) {
      if (className.equals(keyword))
      {
        className = className + "%";
        break;
      }
    }
    StringBuilder modifiedClassName = new StringBuilder(className.length());
    
    char firstChar = className.charAt(0);
    if (Character.isJavaIdentifierStart(firstChar)) {
      modifiedClassName.append(firstChar);
    } else {
      modifiedClassName.append(mangleChar(firstChar));
    }
    char[] arrayOfChar = className.substring(1).toCharArray();String keyword = arrayOfChar.length;
    for (String str1 = 0; str1 < keyword; str1++)
    {
      char subChar = arrayOfChar[str1];
      if (Character.isJavaIdentifierPart(subChar)) {
        modifiedClassName.append(subChar);
      } else {
        modifiedClassName.append(mangleChar(subChar));
      }
    }
    return modifiedClassName.toString();
  }
  
  private String stripExtension(File jspFile)
  {
    return StringUtils.removeSuffix(jspFile.getName(), ".jsp");
  }
  
  private static String mangleChar(char ch)
  {
    if (ch == File.separatorChar) {
      ch = '/';
    }
    String s = Integer.toHexString(ch);
    int nzeros = 5 - s.length();
    char[] result = new char[6];
    result[0] = '_';
    for (int i = 1; i <= nzeros; i++) {
      result[i] = '0';
    }
    int resultIndex = 0;
    for (int i = nzeros + 1; i < 6; i++) {
      result[i] = s.charAt(resultIndex++);
    }
    return new String(result);
  }
  
  public String mapPath(String path)
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.JspNameMangler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */