//import com.propertypal.shared;

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

    NetServer() throws IllegalArgumentException, IOException
    {
        //Spawn new HTTP server on port 80
        this(80);
    }

    NetServer(int port) throws IllegalArgumentException, IOException
    {
        if (port < 0 || port > 65535) throw new IllegalArgumentException("Invalid port number given");

        this.port = port;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        executor = Executors.newFixedThreadPool(10);

        //Add endpoints
        //httpServer.createContext("/helloWorld", new EndpointHelloWorld());
        //httpServer.createContext("/echoTest", new EndpointEcho());
        httpServer.createContext("/login", new Endpoint(LoginPacket.class, (request) -> { filter.AuthFilter.filterLoginPacket(request); }));

        //httpServer.setExecutor(executor); //Multithreaded. Use for prod
        httpServer.setExecutor(null); //Singlethreaded. Use for easier debug

        httpServer.start();
    }

    public void close()
    {
        executor.shutdown();
    }
}
