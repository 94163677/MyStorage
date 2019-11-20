package air.kanna.mystorage.sync;

import java.net.Inet4Address;
import java.util.List;

import air.kanna.mystorage.sync.util.NetworkUtil;

public class TestNetworkUtil {

    public static void main(String[] args) {
        try {
            List<Inet4Address> addrs = NetworkUtil.getLocalIpv4Address();
            System.out.println();
            System.out.println();
            for(Inet4Address add : addrs) {
                System.out.println(add.getHostAddress());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
