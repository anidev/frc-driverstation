package org.anidev.frcds.proto.torobot;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.anidev.frcds.proto.CommData;

public class CRIOChecksum extends CommData {
	public static final int SIZE=8;
	private long checksum;

	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum=checksum;
	}

	@Override
	public byte[] serialize() {
		ByteBuffer buffer=ByteBuffer.allocate(8);
		buffer.order(BYTE_ORDER);
		buffer.putLong(checksum);
		return buffer.array();
	}

	@Override
	public void deserialize(byte[] data) {
		ByteBuffer byteBuffer=ByteBuffer.wrap(data);
		byteBuffer.order(BYTE_ORDER);
		LongBuffer longBuffer=byteBuffer.asLongBuffer();
		checksum=longBuffer.get(0);
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result+(int)(checksum^(checksum>>>32));
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
		CRIOChecksum other=(CRIOChecksum)obj;
		if(checksum!=other.checksum) {
			return false;
		}
		return true;
	}

}
