import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TestBench
{
    public static void main(String[] args) throws java.lang.InterruptedException, IOException
    {
        Backend httpServer = new Backend(678);
        System.out.println("Server started on port " + Integer.toString(httpServer.getPort()));

        Thread.currentThread().join(); //Sleep indefinitely

        httpServer.close();
    }
}

class Backend
{
	private HttpServer httpServer;
	private int port;
    private ExecutorService executor;

    public int getPort() { return port; }
	
	Backend() throws IllegalArgumentException, IOException
	{
		//Spawn new HTTP server on port 80
		this(80);
	}
	
	Backend(int port) throws IllegalArgumentException, IOException
    {
        if (port < 0 || port > 65535) throw new IllegalArgumentException("Invalid port number given");

        this.port = port;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        executor = Executors.newFixedThreadPool(10);

        //Add endpoints
        httpServer.createContext("/helloWorld", new EndpointHelloWorld());
        httpServer.createContext("/echoTest", new EndpointEcho());

        httpServer.setExecutor(executor);

        httpServer.start();
    }

    public void close()
    {
        executor.shutdown();
    }
}

class EndpointHelloWorld implements HttpHandler
{
	public void handle(HttpExchange t) throws IOException
	{
		//Get request data
		InputStream inData = t.getRequestBody();
        URI reqURI = t.getRequestURI();
        System.out.println("Got request path: " + reqURI.getPath() + " with query: " + reqURI.getQuery());
		System.out.println("Got request data: " + new String(inData.readAllBytes()));
		
		//Respond
		String response = "Hello world";
		t.sendResponseHeaders(200, response.length());
		OutputStream outData = t.getResponseBody();
		outData.write(response.getBytes());
		outData.close();

        System.out.println("Responded with: " + response);
	}
}

class EndpointEcho implements HttpHandler
{
    public void handle(HttpExchange t) throws IOException
    {
        //Get request data
        InputStream inData = t.getRequestBody();
        URI reqURI = t.getRequestURI();
        String reqData = new String(inData.readAllBytes());

        System.out.println("Got request path: " + reqURI.getPath() + " with query: " + reqURI.getQuery());
        System.out.println("Got request data: " + reqData);

        //Respond
        t.sendResponseHeaders(200, reqData.length());
        OutputStream outData = t.getResponseBody();
        outData.write(reqData.getBytes());
        outData.close();

        System.out.println("Responded with: " + reqData);
    }
}