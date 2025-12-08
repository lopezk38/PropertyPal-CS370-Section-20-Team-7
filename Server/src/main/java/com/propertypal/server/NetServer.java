package com.propertypal.server;

import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.propertypal.shared.network.packets.*;

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
        //accounts
        httpServer.createContext("/auth/login", new Endpoint(LoginPacket.class, (request) -> { filter.filterLoginPacket(request); }));
        httpServer.createContext("/auth/logout", new Endpoint(LogoutPacket.class, (request) -> { filter.filterLogoutPacket(request); }));
        httpServer.createContext("/auth/newAcct/tenant", new Endpoint(CreateAcctPacket.class, (request) -> { filter.filterCreateTenantAcctPacket(request); }));
        httpServer.createContext("/auth/newAcct/landlord", new Endpoint(CreateAcctPacket.class, (request) -> { filter.filterCreateLandlordAcctPacket(request); }));
        httpServer.createContext("/account/getLease", new Endpoint(GetAcctLeasePacket.class, (request) -> { filter.filterGetAcctLeasePacket(request); }));

        //invites
        httpServer.createContext("/lease/genInvite", new Endpoint(CreateInvitePacket.class, (request) -> { filter.filterCreateInvitePacket(request); }));
        httpServer.createContext("/lease/acceptInvite", new Endpoint(AcceptInvitePacket.class, (request) -> { filter.filterAcceptInvitePacket(request); }));
        httpServer.createContext("/lease/getInvites", new Endpoint(GetInviteListPacket.class, (request) -> { filter.filterGetInviteListPacket(request); }));

        //docs
        httpServer.createContext("/doc/upload", new Endpoint(UploadDocPacket.class, (request) -> { filter.filterUploadDocPacket(request); }));
        httpServer.createContext("/doc/view", new Endpoint(ViewDocPacket.class, (ClientRequest request) -> { filter.filterViewDocPacket(request); }));
        httpServer.createContext("/doc/delete", new Endpoint(DeleteDocPacket.class, (ClientRequest request) -> { filter.filterDeleteDocPacket(request); }));
        httpServer.createContext("/doc/info", new Endpoint(GetDocInfoPacket.class, (ClientRequest request) -> { filter.filterGetDocInfoPacket(request); }));
        httpServer.createContext("/doc/list", new Endpoint(GetDocListPacket.class, (ClientRequest request) -> { filter.filterGetDocListPacket(request); }));

        //tickets
        httpServer.createContext("/ticket/new", new Endpoint(CreateTicketPacket.class, (request) -> { filter.filterCreateTicketPacket(request); }));
        httpServer.createContext("/ticket/view", new Endpoint(ViewTicketPacket.class, (request) -> { filter.filterViewTicketPacket(request); }));
        httpServer.createContext("/ticket/edit", new Endpoint(EditTicketPacket.class, (request) -> { filter.filterEditTicketPacket(request); }));
        httpServer.createContext("/ticket/list", new Endpoint(GetTicketListPacket.class, (request) -> { filter.filterGetTicketListPacket(request); }));
        httpServer.createContext("/ticket/info", new Endpoint(GetTicketInfoPacket.class, (request) -> { filter.filterGetTicketInfoPacket(request); }));

        //payments
        httpServer.createContext("/lease/reqRent", new Endpoint(RequestRentPacket.class, (request) -> { filter.filterRequestRentPacket(request); }));
        httpServer.createContext("/lease/setupRent", new Endpoint(UpdateAmountDuePacket.class, (request) -> { filter.filterUpdAmountDuePacket(request); }));
        httpServer.createContext("/lease/getPayLink", new Endpoint(GetPayLinkPacket.class, (request) -> { filter.filterGetPayLinkPacket(request); }));

        //navigation
        httpServer.createContext("/lease/getRole", new Endpoint(GetRolePacket.class, (request) -> { filter.filterGetRolePacket(request); }));
        httpServer.createContext("/lease/getContacts", new Endpoint(GetLeaseContactsPacket.class, (request) -> { filter.filterGetLeaseContactsPacket(request); }));

        httpServer.setExecutor(executor); //Multithreaded. Use for prod
        //httpServer.setExecutor(null); //Singlethreaded. Use for easier debug

        httpServer.start();
    }

    public void close()
    {
        executor.shutdown();
    }
}
