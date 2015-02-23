package org.anidev.frcds.protoold.torobot;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.anidev.frcds.protoold.CommData;

public class FPGAChecksum extends CommData {
	public static final int SIZE=16;
	int[] checksum=new int[] {0,0,0,0};

	public int[] getChecksum() {
		return checksum;
	}

	public void setChecksum(int[] checksum) {
		this.checksum=checksum;
	}

	@Override
	public byte[] serialize() {
		ByteBuffer buffer=ByteBuffer.allocate(16);
		buffer.order(BYTE_ORDER);
		for(int i=0;i<checksum.length;i++) {
			buffer.putInt(checksum[i]);
		}
		return buffer.array();
	}

	@Override
	public void deserialize(byte[] data) {
		ByteBuffer byteBuffer=ByteBuffer.wrap(data);
		byteBuffer.order(BYTE_ORDER);
		IntBuffer intBuffer=byteBuffer.asIntBuffer();
		int[] ints=new int[4];
		for(int i=0;i<ints.length;i++) {
			ints[i]=intBuffer.get(i);
		}
		checksum=ints;
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+Arrays.hashCode(checksum);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			return false;
		}
		FPGAChecksum other=(FPGAChecksum)obj;
		if(!Arrays.equals(checksum,other.checksum)) {
			return false;
		}
		return true;
	}

}
