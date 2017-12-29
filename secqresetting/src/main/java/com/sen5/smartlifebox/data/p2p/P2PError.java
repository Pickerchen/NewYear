package com.sen5.smartlifebox.data.p2p;

import com.p2p.pppp_api.PPCS_APIs;

public class P2PError {
	
	public static String errorString(int ret) {
		
		String error = "";
		
		switch(ret) {
		case PPCS_APIs.ERROR_PPCS_NOT_INITIALIZED:
			error = ret + "::ERROR_PPCS_NOT_INITIALIZED";
			break;
			
		case PPCS_APIs.ERROR_PPCS_ALREADY_INITIALIZED:
			error = ret + "::ERROR_PPCS_ALREADY_INITIALIZED";
			break;
			
		case PPCS_APIs.ERROR_PPCS_TIME_OUT:
			error = ret + "::ERROR_PPCS_TIME_OUT";
			break;
			
		case PPCS_APIs.ERROR_PPCS_INVALID_ID:
			error = ret + "::ERROR_PPCS_INVALID_ID";
			break;
			
		case PPCS_APIs.ERROR_PPCS_INVALID_PARAMETER:
			error = ret + "::ERROR_PPCS_INVALID_PARAMETER";
			break;
			
		case PPCS_APIs.ERROR_PPCS_DEVICE_NOT_ONLINE:
			error = ret + "::ERROR_PPCS_DEVICE_NOT_ONLINE";
			break;
			
		case PPCS_APIs.ERROR_PPCS_FAIL_TO_RESOLVE_NAME:
			error = ret + "::ERROR_PPCS_FAIL_TO_RESOLVE_NAME";
			break;
			
		case PPCS_APIs.ERROR_PPCS_INVALID_PREFIX:
			error = ret + "::ERROR_PPCS_INVALID_PREFIX";
			break;
			
		case PPCS_APIs.ERROR_PPCS_ID_OUT_OF_DATE:
			error = ret + "::ERROR_PPCS_ID_OUT_OF_DATE";
			break;
			
		case PPCS_APIs.ERROR_PPCS_NO_RELAY_SERVER_AVAILABLE:
			error = ret + "::ERROR_PPCS_NO_RELAY_SERVER_AVAILABLE";
			break;
			
		case PPCS_APIs.ERROR_PPCS_INVALID_SESSION_HANDLE:
			error = ret + "::ERROR_PPCS_INVALID_SESSION_HANDLE";
			break;
			
		case PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_REMOTE:
			error = ret + "::ERROR_PPCS_SESSION_CLOSED_REMOTE";
			break;
			
		case PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_TIMEOUT:
			error = ret + "::ERROR_PPCS_SESSION_CLOSED_TIMEOUT";
			break;
			
		case PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_CALLED:
			error = ret + "::ERROR_PPCS_SESSION_CLOSED_CALLED";
			break;
			
		case PPCS_APIs.ERROR_PPCS_REMOTE_SITE_BUFFER_FULL:
			error = ret + "::ERROR_PPCS_REMOTE_SITE_BUFFER_FULL";
			break;
			
		case PPCS_APIs.ERROR_PPCS_USER_LISTEN_BREAK:
			error = ret + "::ERROR_PPCS_USER_LISTEN_BREAK";
			break;
			
		case PPCS_APIs.ERROR_PPCS_MAX_SESSION:
			error = ret + "::ERROR_PPCS_MAX_SESSION";
			break;
			
		case PPCS_APIs.ERROR_PPCS_UDP_PORT_BIND_FAILED:
			error = ret + "::ERROR_PPCS_UDP_PORT_BIND_FAILED";
			break;
			
		case PPCS_APIs.ERROR_PPCS_USER_CONNECT_BREAK:
			error = ret + "::ERROR_PPCS_USER_CONNECT_BREAK";
			break;
			
		case PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_INSUFFICIENT_MEMORY:
			error = ret + "::ERROR_PPCS_SESSION_CLOSED_INSUFFICIENT_MEMORY";
			break;
			
		case PPCS_APIs.ERROR_PPCS_INVALID_APILICENSE:
			error = ret + "::ERROR_PPCS_INVALID_APILICENSE";
			break;
			
		case PPCS_APIs.ER_ANDROID_NULL:
		default:
			error = ret + "::ER_ANDROID_NULL";
			break;
		}
		
		return error;
	}
	
}










