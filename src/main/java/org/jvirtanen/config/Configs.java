package org.jvirtanen.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class contains methods for manipulating configuration objects.
 */
public class Configs {

    private Configs() {
    }

    /**
     * Get an IP address. The configuration value can either be a hostname or
     * a literal IP address.
     *
     * @param config a configuration object
     * @param path the path expression
     * @return an IP address
     * @throws ConfigException.Missing if the value is absent or null
     * @throws ConfigException.WrongType if the value is not convertible to
     *   a string
     * @throws ConfigException.BadValue if the value cannot be translated into
     *   an IP address
     */
    public static InetAddress getInetAddress(Config config, String path) {
        try {
            return InetAddress.getByName(config.getString(path));
        } catch (UnknownHostException e) {
            throw badValue(e, config, path);
        }
    }

    /**
     * Get a network interface. The network interface can be identified by its
     * name or its IP address.
     *
     * @param config a configuration object
     * @param path the path expression
     * @return a network interface
     * @throws ConfigException.Missing if the value is absent or null
     * @throws ConfigException.WrongType if the value is not convertible to
     *   a string
     * @throws ConfigException.BadValue if the value cannot be translated
     *   into a network interface
     */
    public static NetworkInterface getNetworkInterface(Config config, String path) {
        NetworkInterface value = getNetworkInterfaceByName(config, path);
        if (value == null)
            value = getNetworkInterfaceByInetAddress(config, path);

        if (value == null)
            throw badValue("No network interface for value '" + config.getString(path) + "'", config, path);

        return value;
    }

    /**
     * Get a port number.
     *
     * @param config a configuration object
     * @param path the path expression
     * @return a port number
     * @throws ConfigException.Missing if the value is absent or null
     * @throws ConfigException.WrongType if the value is not convertible to
     *   an integer
     * @throws ConfigException.BadValue if the value is outside the port
     *   number range
     */
    public static int getPort(Config config, String path) {
        try {
            return new InetSocketAddress(config.getInt(path)).getPort();
        } catch (IllegalArgumentException e) {
            throw badValue(e, config, path);
        }
    }

    private static NetworkInterface getNetworkInterfaceByInetAddress(Config config, String path) {
        try {
            return NetworkInterface.getByInetAddress(getInetAddress(config, path));
        } catch (SocketException e) {
            throw badValue(e, config, path);
        }
    }

    private static NetworkInterface getNetworkInterfaceByName(Config config, String path) {
        try {
            return NetworkInterface.getByName(config.getString(path));
        } catch (SocketException e) {
            throw badValue(e, config, path);
        }
    }

    private static ConfigException badValue(String message, Config config, String path) {
        return new ConfigException.BadValue(config.getValue(path).origin(), path, message);
    }

    private static ConfigException badValue(Exception cause, Config config, String path) {
        return new ConfigException.BadValue(config.getValue(path).origin(), path, cause.getMessage(), cause);
    }

}
