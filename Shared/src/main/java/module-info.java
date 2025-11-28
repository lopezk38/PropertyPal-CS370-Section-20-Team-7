module com.propertypal.shared.network {
	requires com.google.gson;
    requires java.sql;

    exports com.propertypal.shared.network;
	exports com.propertypal.shared.network.enums;
	exports com.propertypal.shared.network.packets;
	exports com.propertypal.shared.network.responses;
}