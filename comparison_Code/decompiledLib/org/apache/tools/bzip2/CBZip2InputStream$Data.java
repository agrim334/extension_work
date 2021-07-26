package org.apache.tools.bzip2;

final class CBZip2InputStream$Data
{
  final boolean[] inUse = new boolean['Ā'];
  final byte[] seqToUnseq = new byte['Ā'];
  final byte[] selector = new byte['䙒'];
  final byte[] selectorMtf = new byte['䙒'];
  final int[] unzftab = new int['Ā'];
  final int[][] limit = new int[6]['Ă'];
  final int[][] base = new int[6]['Ă'];
  final int[][] perm = new int[6]['Ă'];
  final int[] minLens = new int[6];
  final int[] cftab = new int['ā'];
  final char[] getAndMoveToFrontDecode_yy = new char['Ā'];
  final char[][] temp_charArray2d = new char[6]['Ă'];
  final byte[] recvDecodingTables_pos = new byte[6];
  int[] tt;
  byte[] ll8;
  
  CBZip2InputStream$Data(int blockSize100k)
  {
    ll8 = new byte[blockSize100k * 100000];
  }
  
  final int[] initTT(int length)
  {
    int[] ttShadow = tt;
    if ((ttShadow == null) || (ttShadow.length < length)) {
      tt = (ttShadow = new int[length]);
    }
    return ttShadow;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.bzip2.CBZip2InputStream.Data
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */