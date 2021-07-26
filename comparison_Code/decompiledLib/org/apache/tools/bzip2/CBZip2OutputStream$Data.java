package org.apache.tools.bzip2;

final class CBZip2OutputStream$Data
{
  final boolean[] inUse = new boolean['Ā'];
  final byte[] unseqToSeq = new byte['Ā'];
  final int[] mtfFreq = new int['Ă'];
  final byte[] selector = new byte['䙒'];
  final byte[] selectorMtf = new byte['䙒'];
  final byte[] generateMTFValues_yy = new byte['Ā'];
  final byte[][] sendMTFValues_len = new byte[6]['Ă'];
  final int[][] sendMTFValues_rfreq = new int[6]['Ă'];
  final int[] sendMTFValues_fave = new int[6];
  final short[] sendMTFValues_cost = new short[6];
  final int[][] sendMTFValues_code = new int[6]['Ă'];
  final byte[] sendMTFValues2_pos = new byte[6];
  final boolean[] sentMTFValues4_inUse16 = new boolean[16];
  final int[] heap = new int['Ą'];
  final int[] weight = new int['Ȅ'];
  final int[] parent = new int['Ȅ'];
  final byte[] block;
  final int[] fmap;
  final char[] sfmap;
  int origPtr;
  
  CBZip2OutputStream$Data(int blockSize100k)
  {
    int n = blockSize100k * 100000;
    block = new byte[n + 1 + 20];
    fmap = new int[n];
    sfmap = new char[2 * n];
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.bzip2.CBZip2OutputStream.Data
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */