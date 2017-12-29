package nes.ltlib.interf;

/**
 * Created by ZHOUDAO on 2017/8/30.
 */

public interface DataChannelListener {

    void onConnecting();

    void onConnected(int var1, String var2, int var3);

    void onReConnecting();

    void onDisconnected(int var1);

}
