package org.anidev.frcds.protoold;

import java.nio.ByteOrder;

public abstract class CommData {
	public static int SIZE=-1;
	public static final ByteOrder BYTE_ORDER=ByteOrder.BIG_ENDIAN;

	public abstract byte[] serialize();

	public abstract void deserialize(byte[] data);

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	protected int boolToInt(boolean bool) {
		return(bool?1:0);
	}

	protected boolean intToBool(int bool) {
		return(bool>0?true:false);
	}

	protected int[] bitsToInts(boolean[] bits) {
		int numInts=(int)Math.ceil(bits.length/8.0);
		int[] data=new int[numInts];
		for(int i=0;i<numInts;i++) {
			for(int b=0;b<Math.min(bits.length-8*i,8);b++) {
				data[i]+=(boolToInt(bits[i*8+b])<<(7-b));
			}
		}
		return data;
	}

	protected boolean[] intsToBits(int[] ints) {
		boolean[] bools=new boolean[ints.length*8];
		for(int i=0;i<ints.length;i++) {
			for(int b=0;b<8;b++) {
				bools[i*8+b]=intToBool(0x1&(ints[i]>>(7-b)));
			}
		}
		return bools;
	}
}
