package org.jvirtanen.config;

import static org.junit.Assert.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.junit.Test;

public class ConfigsTest {

    @Test
    public void inetAddressByAddress() throws Exception {
        Config config = config("server.address = 127.0.0.1");

        InetAddress address = InetAddress.getByName("127.0.0.1");

        assertEquals(address, Configs.getInetAddress(config, "server.address"));
    }

    @Test
    public void inetAddressByName() throws Exception {
        Config config = config("server.address = localhost");

        InetAddress address = InetAddress.getByName("localhost");

        assertEquals(address, Configs.getInetAddress(config, "server.address"));
    }

    @Test(expected=ConfigException.Missing.class)
    public void missingInetAddress() throws Exception {
        Configs.getInetAddress(config(""), "server.address");
    }

    @Test(expected=ConfigException.WrongType.class)
    public void wrongTypeInetAddress() throws Exception {
        Config config = config("server.address = 127.0.0.1");

        Configs.getInetAddress(config, "server");
    }

    @Test(expected=ConfigException.BadValue.class)
    public void badValueInetAddress() throws Exception {
        Config config = config("server.address = <none>");

        Configs.getInetAddress(config, "server.address");
    }

    @Test
    public void networkInterfaceByInetAddress() throws Exception {
        Config config = config("server.network-interface = 127.0.0.1");

        assertEquals(loopback(), Configs.getNetworkInterface(config, "server.network-interface"));
    }

    @Test
    public void networkInterfaceByName() throws Exception {
        Config config = config("server.network-interface = " + loopback().getName());

        assertEquals(loopback(), Configs.getNetworkInterface(config, "server.network-interface"));
    }

    @Test(expected=ConfigException.Missing.class)
    public void missingNetworkInterface() throws Exception {
        Configs.getNetworkInterface(config(""), "server.network-interface");
    }

    @Test(expected=ConfigException.WrongType.class)
    public void wrongTypeNetworkInterface() throws Exception {
        Config config = config("server.network-interface = " + loopback().getName());

        Configs.getNetworkInterface(config, "server");
    }

    @Test(expected=ConfigException.BadValue.class)
    public void badValueNetworkInterface() throws Exception {
        Config config = config("server.network-interface = <none>");

        Configs.getNetworkInterface(config, "server.network-interface");
    }

    @Test
    public void port() throws Exception {
        Config config = config("server.port = 4000");

        assertEquals(4000, Configs.getPort(config, "server.port"));
    }

    @Test(expected=ConfigException.Missing.class)
    public void missingPort() throws Exception {
        Configs.getPort(config(""), "server.port");
    }

    @Test(expected=ConfigException.WrongType.class)
    public void wrongTypePort() throws Exception {
        Config config = config("server.port = 4000");

        Configs.getPort(config, "server");
    }

    @Test(expected=ConfigException.BadValue.class)
    public void badValuePort() throws Exception {
        Config config = config("server.port = -1");

        Configs.getPort(config, "server.port");
    }

    private Config config(String s) {
        return ConfigFactory.parseString(s);
    }

    private NetworkInterface loopback() throws Exception {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

        while (e.hasMoreElements()) {
            NetworkInterface networkInterface = e.nextElement();
            if (networkInterface.isLoopback())
                return networkInterface;
        }

        throw new IllegalStateException("No loopback interface");
    }

}
