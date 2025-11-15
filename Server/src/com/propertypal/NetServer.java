package com.propertypal;

import com.propertypal.SecurityFilter;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.propertypal.network.packets.*;

public class NetServer
{
    private HttpServer httpServer;
    private int port;
    private ExecutorService executor;

    private SecurityFilter filter = SecurityFilter.getInstance();

    public int getPort() { return port; }

    private NetServer() throws IllegalArgumentException, IOException
    {
        //Spawn new HTTP server on port 80
        this(80);
    }

    public NetServer(int port) throws IllegalArgumentException, IOException
    {
        if (port < 0 || port > 65535) throw new IllegalArgumentException("Invalid port number given");

        this.port = port;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        executor = Executors.newFixedThreadPool(10);

        //Add endpoints
        httpServer.createContext("/auth/login", new Endpoint(LoginPacket.class, (request) -> { filter.filterLoginPacket(request); }));
        httpServer.createContext("/auth/logout", new Endpoint(LogoutPacket.class, (request) -> { filter.filterLogoutPacket(request); }));
        httpServer.createContext("/account/create", new Endpoint(CreateAcctPacket.class, (request) -> { filter.filterCreateAcctPacket(request); }));
        httpServer.createContext("/invite/create", new Endpoint(CreateInvitePacket.class, (request) -> { filter.filterCreateInvitePacket(request); }));

        //httpServer.setExecutor(executor); //Multithreaded. Use for prod
        httpServer.setExecutor(null); //Singlethreaded. Use for easier debug

        httpServer.start();
    }

    public void close()
    {
        executor.shutdown();
    }
}
