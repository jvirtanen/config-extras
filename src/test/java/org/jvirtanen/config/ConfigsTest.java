package org.jvirtanen.config;

import static org.junit.Assert.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConfigsTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

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

    @Test
    public void missingInetAddress() throws Exception {
        exception.expect(ConfigException.Missing.class);

        Configs.getInetAddress(config(""), "server.address");
    }

    @Test
    public void wrongTypeInetAddress() throws Exception {
        Config config = config("server.address = 127.0.0.1");

        exception.expect(ConfigException.WrongType.class);

        Configs.getInetAddress(config, "server");
    }

    @Test
    public void badValueInetAddress() throws Exception {
        Config config = config("server.address = <none>");

        exception.expect(ConfigException.BadValue.class);

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

    @Test
    public void missingNetworkInterface() throws Exception {
        exception.expect(ConfigException.Missing.class);

        Configs.getNetworkInterface(config(""), "server.network-interface");
    }

    @Test
    public void wrongTypeNetworkInterface() throws Exception {
        Config config = config("server.network-interface = " + loopback().getName());

        exception.expect(ConfigException.WrongType.class);

        Configs.getNetworkInterface(config, "server");
    }

    @Test
    public void badValueNetworkInterface() throws Exception {
        Config config = config("server.network-interface = <none>");

        exception.expect(ConfigException.BadValue.class);

        Configs.getNetworkInterface(config, "server.network-interface");
    }

    @Test
    public void port() throws Exception {
        Config config = config("server.port = 4000");

        assertEquals(4000, Configs.getPort(config, "server.port"));
    }

    @Test
    public void missingPort() throws Exception {
        exception.expect(ConfigException.Missing.class);

        Configs.getPort(config(""), "server.port");
    }

    @Test
    public void wrongTypePort() throws Exception {
        Config config = config("server.port = 4000");

        exception.expect(ConfigException.WrongType.class);

        Configs.getPort(config, "server");
    }

    @Test
    public void badValuePort() throws Exception {
        Config config = config("server.port = -1");

        exception.expect(ConfigException.BadValue.class);

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
