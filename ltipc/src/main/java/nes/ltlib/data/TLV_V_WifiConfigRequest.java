package nes.ltlib.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TLV_V_WifiConfigRequest {
	public byte[] name = new byte[32];
	public byte[] ssid = new byte[32];
	public byte[] password = new byte[128];// change 32 -> 64 08-01-2015


	public byte[] serialize() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.write(name, 0, 32);
		dos.write(ssid, 0, 32);
		dos.write(password, 0, 128);

		baos.close();
		dos.close();
		return baos.toByteArray();
	}
	
	public byte[] OperateType=new byte[32];
	public byte[] DeviceType=new byte[32];
	public  byte[] serializeLearn() throws IOException{
		ByteArrayOutputStream baosLearn = new ByteArrayOutputStream();
		DataOutputStream dosLearn = new DataOutputStream(baosLearn);

		dosLearn.write(name, 0, 32);
		dosLearn.write(ssid, 0, 32);
		dosLearn.write(password, 0, 128);

		baosLearn.close();
		dosLearn.close();
		return baosLearn.toByteArray();
	}

}
