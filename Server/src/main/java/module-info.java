module com.propertypal.server {
    requires jdk.httpserver;
    requires com.google.gson;
    requires java.sql;
    requires com.propertypal.shared.network;

    exports com.propertypal.server;
}