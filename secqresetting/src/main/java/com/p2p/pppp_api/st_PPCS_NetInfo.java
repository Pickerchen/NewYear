package com.p2p.pppp_api;

public class st_PPCS_NetInfo {
	byte bFlagInternet		=0;
	byte bFlagHostResolved	=0;
	byte bFlagServerHello	=0;
	byte NAT_Type			=0;
	byte[] MyLanIP=new byte[16];
	byte[] MyWanIP=new byte[16];
	
	public String getMyLanIP() { return st_PPCS_Session.bytes2Str(MyLanIP); }
	public String getMyWanIP() { return st_PPCS_Session.bytes2Str(MyWanIP); }	
}
