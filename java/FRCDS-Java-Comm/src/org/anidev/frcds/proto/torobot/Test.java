package org.anidev.frcds.proto.torobot;

import java.io.FileInputStream;

public class Test {
	public static void main(String[] args) throws Exception {
		FileInputStream inputStream=new FileInputStream("/home/anirudh/Downloads/raw");
		byte[] data=new byte[FRCCommonControl.SIZE];
		inputStream.read(data);
		FRCCommonControl frcData=new FRCCommonControl();
		frcData.deserialize(data);
		inputStream.close();
	}
}
