package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

public enum MethodHandleCPInfo$ReferenceKind
{
  REF_getField,  REF_getStatic,  REF_putField,  REF_putStatic,  REF_invokeVirtual,  REF_invokeStatic,  REF_invokeSpecial,  REF_newInvokeSpecial,  REF_invokeInterface;
  
  private MethodHandleCPInfo$ReferenceKind() {}
  
  public int value()
  {
    return ordinal() + 1;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodHandleCPInfo.ReferenceKind
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */