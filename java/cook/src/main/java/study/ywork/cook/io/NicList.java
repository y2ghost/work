package study.ywork.cook.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

// 列出网卡列表示例
public class NicList {
    public static void main(String[] a) throws IOException {
        Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
        while (nics.hasMoreElements()) {
            NetworkInterface iface = nics.nextElement();
            System.out.println(iface.getDisplayName());
            Enumeration<InetAddress> addrs = iface.getInetAddresses();

            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                System.out.println(addr);
            }
        }

        InetAddress destAddr = InetAddress.getByName("localhost");
        try {
            NetworkInterface dest = NetworkInterface.getByInetAddress(destAddr);
            System.out.println("Address for " + destAddr + " is " + dest);
        } catch (SocketException ex) {
            System.err.println("Couldn't get address for " + destAddr);
        }
    }
}
