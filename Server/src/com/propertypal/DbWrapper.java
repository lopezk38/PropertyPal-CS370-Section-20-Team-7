package com.propertypal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.io.IOException;

public class DbWrapper
{
    private static DbWrapper instance = null;

    private HashMap<PreparedStatement, Connection> openConnections = new HashMap<PreparedStatement, Connection>();

    private final String dbCredsPath = "./data/creds.cfg";

    private final String dbUrl = "jdbc:h2:./data/db;CIPHER=AES";
    private final String defDbUName = "propertypalAdmin";

    private String dbUName = null;
    private String dbPw = null;

    public static DbWrapper getInstance()
    {
        if (instance == null)
        {
            instance = new DbWrapper();
        }

        return instance;
    }

    private DbWrapper()
    {
        if (instance != null) return;

        instance = this;

        //Load config file
        try
        {
            Path creds = Path.of(dbCredsPath);
            String[] credTokens = Files.readString(creds).split("\n");
            if (credTokens.length == 2 && !credTokens[0].isBlank() && !credTokens[1].isBlank())
            {
                dbUName = credTokens[0];
                dbPw = credTokens[1];
            }
            else
            {
                //Malformed credential file
                System.out.printf("ERROR: Credential file (%s) is malformed or corrupted. If it was manually edited, please ensure that the first line has the username and the second line has the DB encryption password, a single space, and the DB account password. There should only be two lines in the whole file.%n", dbCredsPath);
                System.exit(-1);
            }
        }
        catch (IOException e)
        {
            //Credential file did not exist
            //Check if DB file exists
            Path dbFile = Path.of("./data/db.mv.db");
            if (!Files.exists(dbFile))
            {
                //No db file. This must be the first run of this program
                //Generate credentials
                dbUName = defDbUName;
                dbPw = genPW();
                try
                {
                    Path credFile = Path.of(dbCredsPath);
                    Files.writeString(credFile, dbUName + "\n");
                    Files.writeString(credFile, dbPw, StandardOpenOption.APPEND);
                }
                catch (IOException e2)
                {
                    //Failed to gen creds file, abort
                    System.out.printf("ERROR: Credential file could not be generated at %s. Ensure your drive is not full and the directory has write permissions%n", dbCredsPath);
                    System.exit(-1);
                }
            }
            else
            {
                //A db already exists. Can't load without its password
                System.out.printf("ERROR: Credential file (%s) is missing. Add it and ensure that the first line has the username and the second line has the DB encryption password, a single space, and the DB account password. There should only be two lines in the whole file.%n", dbCredsPath);
                System.exit(-1);
            }
        }

        //Make sure creds are good
        try
        {
            Connection con = DriverManager.getConnection(dbUrl, dbUName, dbPw);
            con.close();
        }
        catch (SQLException e)
        {
            System.out.printf("ERROR: Incorrect credentials for database. Enter the correct credentials into %s and retry%n", dbCredsPath);
            System.exit(-1);
        }

        //Make sure DB is there/gen DB if not
        if (!validateTableFormat())
        {
            //Assume DB is not there/uninitialized
            //TODO Add function to backup DB
            dbInit();
        }
    }

    private String genPW()
    {
        SecureRandom rand = new SecureRandom();
        StringBuilder strBuild = new StringBuilder();
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        int len = 24;

        for (int i = 0; i < len; ++i)
        {
            strBuild.append(charset.charAt(rand.nextInt(charset.length()))); //Get random char from charset, append to builder
        }
        strBuild.append(" ");
        for (int i = 0; i < len; ++i)
        {
            strBuild.append(charset.charAt(rand.nextInt(charset.length()))); //Get random char from charset, append to builder
        }

        return strBuild.toString();
    }

    private void dbInit() throws RuntimeException
    {
        Connection con = establishCon();

        try
        {
            //Drop constraints to allow deletion of tables
            con.createStatement().execute("""
            ALTER TABLE IF EXISTS Properties DROP CONSTRAINT IF EXISTS FK_PROP_LEASE;
            ALTER TABLE IF EXISTS Properties DROP CONSTRAINT IF EXISTS FK_PROP_USERS;
            ALTER TABLE IF EXISTS Invites DROP CONSTRAINT IF EXISTS FK_INVITE_LANDLORD;
            ALTER TABLE IF EXISTS Invites DROP CONSTRAINT IF EXISTS FK_INVITE_TENANT;
            ALTER TABLE IF EXISTS Invites DROP CONSTRAINT IF EXISTS FK_INVITE_PROP;
            ALTER TABLE IF EXISTS Leases DROP CONSTRAINT IF EXISTS FK_LEASES_PROPS;
            ALTER TABLE IF EXISTS Leases DROP CONSTRAINT IF EXISTS FK_LEASES_TENANT;
            ALTER TABLE IF EXISTS DocumentPermissions DROP CONSTRAINT IF EXISTS FK_DOCPERMS_USER;
            ALTER TABLE IF EXISTS DocumentPermissions DROP CONSTRAINT IF EXISTS FK_DOCPERMS_DOC;
            ALTER TABLE IF EXISTS LeasePermissions DROP CONSTRAINT IF EXISTS FK_LEASEPERMS_USER;
            ALTER TABLE IF EXISTS LeasePermissions DROP CONSTRAINT IF EXISTS FK_LEASEPERMS_LEASE;
            ALTER TABLE IF EXISTS Tickets DROP CONSTRAINT IF EXISTS FK_TICKETS_USER;
            ALTER TABLE IF EXISTS Tickets DROP CONSTRAINT IF EXISTS FK_TICKETS_LEASE;
            ALTER TABLE IF EXISTS Comments DROP CONSTRAINT IF EXISTS FK_COMMENT_USER;
            ALTER TABLE IF EXISTS Comments DROP CONSTRAINT IF EXISTS FK_COMMENT_REPLYTO_COMMENT;
            ALTER TABLE IF EXISTS Documents DROP CONSTRAINT IF EXISTS FK_DOCUMENTS_USER;
            ALTER TABLE IF EXISTS Documents DROP CONSTRAINT IF EXISTS FK_DOCUMENTS_LEASE;
            ALTER TABLE IF EXISTS ESignRequests DROP CONSTRAINT IF EXISTS FK_ESIGNREQ_DOC;
            ALTER TABLE IF EXISTS ESignRequests DROP CONSTRAINT IF EXISTS FK_ESIGNREQ_USER;
            ALTER TABLE IF EXISTS ESignatures DROP CONSTRAINT IF EXISTS FK_ESIGN_DOC;
            ALTER TABLE IF EXISTS ESignatures DROP CONSTRAINT IF EXISTS FK_ESIGN_USER;
            ALTER TABLE IF EXISTS TicketAttachmentsMap DROP CONSTRAINT IF EXISTS FK_TAM_DOC;
            ALTER TABLE IF EXISTS TicketAttachmentsMap DROP CONSTRAINT IF EXISTS FK_TAM_TICKET;
            ALTER TABLE IF EXISTS TicketCommentsMap DROP CONSTRAINT IF EXISTS FK_TCM_COMMENT;
            ALTER TABLE IF EXISTS TicketCommentsMap DROP CONSTRAINT IF EXISTS FK_TCM_TICKET;
            ALTER TABLE IF EXISTS CommentAttachmentsMap DROP CONSTRAINT IF EXISTS FK_CAM_DOC;
            ALTER TABLE IF EXISTS CommentAttachmentsMap DROP CONSTRAINT IF EXISTS FK_CAM_COMMENT;
            ALTER TABLE IF EXISTS RentRequests DROP CONSTRAINT IF EXISTS FK_RR_LEASE;
            """);

            //Add tables
            con.createStatement().execute("DROP TABLE IF EXISTS Users");
            con.createStatement().execute("""
                    CREATE Table Users(
                        userID bigint GENERATED BY DEFAULT AS IDENTITY,
                        firstName VARCHAR,
                        lastName VARCHAR,
                        email VARCHAR,
                        billingAddress1 VARCHAR,
                        billingAddress2 VARCHAR,
                        billingCity VARCHAR,
                        billingState VARCHAR,
                        billingZIP VARCHAR,
                        billingCountry VARCHAR,
                        taxID VARCHAR,
                        registerDate timestamp,
                        hashedPW VARCHAR,
                        requirePWReset boolean, 
                        loginAuthToken VARCHAR,
                        loginTokenExpiration timestamp,
                        loginTokenValidIP VARCHAR,
                        paypalMeLink VARCHAR,
                        isLandlord boolean,
                        phone VARCHAR,
                        CONSTRAINT PK_USERS_ID PRIMARY KEY (userID),
                        CONSTRAINT UQ_USERS_EM UNIQUE (email),
                        CONSTRAINT UQ_USERS_TOK UNIQUE (loginAuthToken)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS Properties");
            con.createStatement().execute("""
                    CREATE Table Properties(
                        propertyID bigint GENERATED BY DEFAULT AS IDENTITY,
                        activeLease bigint,
                        owner bigint,
                        addr1 VARCHAR,
                        addr2 VARCHAR,
                        city VARCHAR,
                        state VARCHAR,
                        zipCode VARCHAR,
                        country VARCHAR,
                        CONSTRAINT PK_PROPS_ID PRIMARY KEY (propertyID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS Invites");
            con.createStatement().execute("""
                    CREATE Table Invites(
                        inviteID bigint GENERATED BY DEFAULT AS IDENTITY,
                        landlordID bigint,
                        tenantID bigint,
                        propertyID bigint,
                        dateMade VARCHAR,
                        CONSTRAINT PK_INVITES_ID PRIMARY KEY (inviteID),
                        CONSTRAINT UQ_LL_TNT_LSE UNIQUE (landlordID, tenantID, propertyID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS Leases");
            con.createStatement().execute("""
                CREATE Table Leases(
                    leaseID bigint GENERATED BY DEFAULT AS IDENTITY,
                    associatedProperty bigint,
                    tenantID bigint,
                    active boolean,
                    dateMade timestamp,
                    rentDueDay int,
                    rentAmount numeric(20, 2),
                    rentLastUpdated timestamp,
                    CONSTRAINT PK_LEASES_ID PRIMARY KEY (leaseID)
                )""");

            con.createStatement().execute("DROP TABLE IF EXISTS DocumentPermissions");
            con.createStatement().execute("""
                    CREATE Table DocumentPermissions(
                        userID bigint,
                        docID bigint,
                        view boolean,
                        editName boolean,
                        editContents boolean,
                        delete boolean,
                        addComment boolean,
                        canESign boolean,
                        CONSTRAINT PK_DOCPERMS_ID PRIMARY KEY (userID, docID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS LeasePermissions");
            con.createStatement().execute("""
                    CREATE Table LeasePermissions(
                        userID bigint,
                        leaseID bigint,
                        editName boolean,
                        editAddress boolean,
                        viewPaymentsPage boolean,
                        genericTicketPerms int,
                        maintTicketPerms int,
                        taxTicketPerms int,
                        rentTicketPerms int,
                        viewTaxFinData boolean,
                        addLeaseContract boolean,
                        proposeLeaseChange boolean,
                        viewLeaseContract boolean,
                        addGenericDoc boolean,
                        viewGenericDoc boolean,
                        CONSTRAINT PK_LEASEPERMS_ID PRIMARY KEY (userID, leaseID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS Tickets");
            con.createStatement().execute("""
                    CREATE Table Tickets(
                        ticketID bigint GENERATED BY DEFAULT AS IDENTITY,
                        owner bigint,
                        parentLease bigint,
                        title VARCHAR,
                        description VARCHAR,
                        dateCreated timestamp,
                        timeModified timestamp,
                        state int,
                        type int,
                        CONSTRAINT PK_TICKETS_ID PRIMARY KEY (ticketID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS Comments");
            con.createStatement().execute("""
                    CREATE Table Comments(
                        commentID bigint GENERATED BY DEFAULT AS IDENTITY,
                        owner bigint,
                        replyTo bigint,
                        dateCreated timestamp,
                        timeModified timestamp,
                        content VARCHAR,
                        isParentDoc boolean,
                        parentID bigint,
                        CONSTRAINT PK_COMMENTS_ID PRIMARY KEY (commentID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS Documents");
            con.createStatement().execute("""
                    CREATE Table Documents(
                        docID bigint GENERATED BY DEFAULT AS IDENTITY,
                        owner bigint,
                        parentLease bigint,
                        docType int,
                        dateCreated timestamp,
                        dateModified timestamp,
                        allowUnauthView boolean,
                        name VARCHAR,
                        description VARCHAR,
                        data blob,
                        fileSize bigint,
                        CONSTRAINT PK_DOCS_ID PRIMARY KEY (docID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS ESignRequests");
            con.createStatement().execute("""
                    CREATE Table ESignRequests(
                        parentDoc bigint,
                        userID bigint,
                        CONSTRAINT PK_ESIGNREQS_ID PRIMARY KEY (parentDoc)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS ESignatures");
            con.createStatement().execute("""
                    CREATE Table ESignatures(
                        parentDoc bigint,
                        signer bigint,
                        token VARCHAR,
                        CONSTRAINT PK_ESIGNS_ID PRIMARY KEY (parentDoc, signer)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS TicketAttachmentsMap");
            con.createStatement().execute("""
                    CREATE Table TicketAttachmentsMap(
                        docID bigint,
                        ticketID bigint,
                        CONSTRAINT PK_TAM_ID PRIMARY KEY (docID, ticketID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS TicketCommentsMap");
            con.createStatement().execute("""
                    CREATE Table TicketCommentsMap(
                        ticketID bigint,
                        commentID bigint,
                        CONSTRAINT PK_TCM_ID PRIMARY KEY (ticketID, commentID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS CommentAttachmentsMap");
            con.createStatement().execute("""
                    CREATE Table CommentAttachmentsMap(
                        docID bigint,
                        commentID bigint,
                        CONSTRAINT PK_CAM_ID PRIMARY KEY (docID, commentID)
                    )""");

            con.createStatement().execute("DROP TABLE IF EXISTS RentRequests");
            con.createStatement().execute("""
                    CREATE Table RentRequests(
                        requestID bigint GENERATED BY DEFAULT AS IDENTITY,
                        leaseID bigint,
                        dateMade VARCHAR,
                        dueDate VARCHAR,
                        amount numeric(20, 2),
                        paid boolean,
                        CONSTRAINT PK_RR_ID PRIMARY KEY (requestID)
                    )""");

            //Add foreign key constraints
            con.createStatement().execute("""
                ALTER Table Properties ADD CONSTRAINT FK_PROP_LEASE FOREIGN KEY (activeLease) REFERENCES Leases (leaseID);
                ALTER Table Properties ADD CONSTRAINT FK_PROP_USERS FOREIGN KEY (owner) REFERENCES Users (userID);
                """);

            con.createStatement().execute("""
                ALTER Table Invites ADD CONSTRAINT FK_INVITE_LANDLORD FOREIGN KEY (landlordID) REFERENCES Users (userID);
                ALTER Table Invites ADD CONSTRAINT FK_INVITE_TENANT FOREIGN KEY (tenantID) REFERENCES Users (userID);
                ALTER Table Invites ADD CONSTRAINT FK_INVITE_PROP FOREIGN KEY (propertyID) REFERENCES Properties (propertyID);
                """);

            con.createStatement().execute("""
                ALTER Table Leases ADD CONSTRAINT FK_LEASES_PROPS FOREIGN KEY (associatedProperty) REFERENCES Properties (propertyID);
                ALTER Table Leases ADD CONSTRAINT FK_LEASES_TENANT FOREIGN KEY (tenantID) REFERENCES Users (userID);
                """);

            con.createStatement().execute("""
                ALTER Table DocumentPermissions ADD CONSTRAINT FK_DOCPERMS_USER FOREIGN KEY (userID) REFERENCES Users (userID);
                ALTER Table DocumentPermissions ADD CONSTRAINT FK_DOCPERMS_DOC FOREIGN KEY (docID) REFERENCES Documents (docID);
                """);

            con.createStatement().execute("""
                ALTER Table LeasePermissions ADD CONSTRAINT FK_LEASEPERMS_USER FOREIGN KEY (userID) REFERENCES Users (userID);
                ALTER Table LeasePermissions ADD CONSTRAINT FK_LEASEPERMS_LEASE FOREIGN KEY (leaseID) REFERENCES Leases (leaseID);
                """);

            con.createStatement().execute("""
                ALTER Table Tickets ADD CONSTRAINT FK_TICKETS_USER FOREIGN KEY (owner) REFERENCES Users (userID);
                ALTER Table Tickets ADD CONSTRAINT FK_TICKETS_LEASE FOREIGN KEY (parentLease) REFERENCES Leases (leaseID);
                """);

            con.createStatement().execute("""
                ALTER Table Comments ADD CONSTRAINT FK_COMMENT_USER FOREIGN KEY (owner) REFERENCES Users (userID);
                ALTER Table Comments ADD CONSTRAINT FK_COMMENT_REPLYTO_COMMENT FOREIGN KEY (replyTo) REFERENCES Comments (commentID);
                """);

            con.createStatement().execute("""
                ALTER Table Documents ADD CONSTRAINT FK_DOCUMENTS_USER FOREIGN KEY (owner) REFERENCES Users (userID);
                ALTER Table Documents ADD CONSTRAINT FK_DOCUMENTS_LEASE FOREIGN KEY (parentLease) REFERENCES Leases (leaseID);
                """);

            con.createStatement().execute("""
                ALTER Table ESignRequests ADD CONSTRAINT FK_ESIGNREQ_DOC FOREIGN KEY (parentDoc) REFERENCES Documents (docID);
                ALTER Table ESignRequests ADD CONSTRAINT FK_ESIGNREQ_USER FOREIGN KEY (userID) REFERENCES Users (userID);
                """);

            con.createStatement().execute("""
                ALTER Table ESignatures ADD CONSTRAINT FK_ESIGN_DOC FOREIGN KEY (parentDoc) REFERENCES Documents (docID);
                ALTER Table ESignatures ADD CONSTRAINT FK_ESIGN_USER FOREIGN KEY (signer) REFERENCES Users (userID);
                """);

            con.createStatement().execute("""
                ALTER Table TicketAttachmentsMap ADD CONSTRAINT FK_TAM_DOC FOREIGN KEY (docID) REFERENCES Documents (docID);
                ALTER Table TicketAttachmentsMap ADD CONSTRAINT FK_TAM_TICKET FOREIGN KEY (ticketID) REFERENCES Tickets (ticketID);
                """);

            con.createStatement().execute("""
                ALTER Table TicketCommentsMap ADD CONSTRAINT FK_TCM_TICKET FOREIGN KEY (ticketID) REFERENCES Tickets (ticketID);
                ALTER Table TicketCommentsMap ADD CONSTRAINT FK_TCM_COMMENT FOREIGN KEY (commentID) REFERENCES Comments (commentID);
                """);

            con.createStatement().execute("""
                ALTER Table CommentAttachmentsMap ADD CONSTRAINT FK_CAM_DOC FOREIGN KEY (docID) REFERENCES Documents (docID);
                ALTER Table CommentAttachmentsMap ADD CONSTRAINT FK_CAM_COMMENT FOREIGN KEY (commentID) REFERENCES Comments (commentID);
                """);

            con.createStatement().execute("""
                ALTER Table RentRequests ADD CONSTRAINT FK_RR_LEASE FOREIGN KEY (leaseID) REFERENCES Leases (leaseID);
                """);

            con.createStatement().execute("""
                CHECKPOINT SYNC;
                """);

            //DEBUG
            PreparedStatement rLL = con.prepareStatement("INSERT INTO Users (email, hashedPW, requirePWReset, isLandlord, phone, firstName, lastName, paypalMeLink) VALUES ('land@lord.com', 'landpass', false, true, '(123) 456-7890', 'Larry', 'Landlord', 'PayPal.Me/123')", Statement.RETURN_GENERATED_KEYS); //Some random paypal account, whoever that is
            rLL.execute();
            ResultSet rLLKey = rLL.getGeneratedKeys();
            rLLKey.next();
            long llID = rLLKey.getLong(1);
            PreparedStatement rTT = con.prepareStatement("INSERT INTO Users (email, hashedPW, requirePWReset, isLandlord, phone, firstName, lastName) VALUES ('ten@ant.com', 'tenpass', false, false, '(098) 765-4321', 'Tony', 'Tenant')", Statement.RETURN_GENERATED_KEYS);
            rTT.execute();
            ResultSet rTTKey = rTT.getGeneratedKeys();
            rTTKey.next();
            long ttID = rTTKey.getLong(1);
            PreparedStatement rProp = con.prepareStatement("INSERT INTO Properties(owner, addr1, addr2, city, state, zipCode, country) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            rProp.setLong(1, llID);
            rProp.setString(2, "1st Street");
            rProp.setString(3, "Suite 1");
            rProp.setString(4, "San Diego");
            rProp.setString(5, "CA");
            rProp.setString(6, "12345");
            rProp.setString(7, "USA");
            rProp.execute();
            ResultSet rPropKey = rProp.getGeneratedKeys();
            rPropKey.next();
            long ptID = rPropKey.getLong(1);
            PreparedStatement rLease = con.prepareStatement("INSERT INTO Leases(associatedProperty, tenantID, active, dateMade, rentDueDay, rentAmount) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            rLease.setLong(1, ptID);
            rLease.setLong(2, ttID);
            rLease.setBoolean(3, true);
            rLease.setString(4, LocalDateTime.now().toString());
            rLease.setInt(5, 1);
            rLease.setBigDecimal(6, new BigDecimal("5.00"));
            rLease.execute();
            ResultSet rLeaseKey = rLease.getGeneratedKeys();
            rLeaseKey.next();
            long leID = rLeaseKey.getLong(1);
            con.createStatement().execute("UPDATE Properties SET activeLease = " + Long.toString(leID) + " WHERE propertyID = " + Long.toString(ptID));
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to init DB due to SQLException: " + e.toString());
            throw new RuntimeException("Could not init DB"); //Rethrow for caller to deal with
        }

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Unable to close DB during initialization");
        }
    }

    private Connection establishCon() throws RuntimeException
    {
        try
        {
            return DriverManager.getConnection(dbUrl, dbUName, dbPw);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to connect to DB");
            throw new RuntimeException("Could not connect to DB");
        }
    }

    private boolean validateTableFormat()
    {
        Connection con = establishCon();

        try
        {
            //Users table
            con.createStatement().execute("""
                    SELECT
                        userID,
                        firstName,
                        lastName,
                        email,
                        billingAddress1,
                        billingAddress2,
                        billingCity,
                        billingState,
                        billingZIP,
                        billingCountry,
                        taxID,
                        registerDate,
                        hashedPW,
                        requirePWReset,
                        loginAuthToken,
                        loginTokenExpiration,
                        loginTokenValidIP,
                        paypalMeLink,
                        isLandlord,
                        phone
                    FROM Users
                    LIMIT 1""");

            //Properties table
            con.createStatement().execute("""
                    SELECT
                        propertyID,
                        activeLease,
                        owner,
                        addr1,
                        addr2,
                        city,
                        state,
                        zipCode,
                        country
                    FROM Properties
                    LIMIT 1""");

            //Invites table
            con.createStatement().execute("""
                    SELECT
                        inviteID,
                        landlordID,
                        tenantID,
                        propertyID,
                        dateMade
                    FROM Invites
                    LIMIT 1""");

            //Leases table
            con.createStatement().execute("""
                    SELECT
                        leaseID,
                        associatedProperty,
                        tenantID,
                        active,
                        dateMade,
                        rentDueDay,
                        rentAmount,
                        rentLastUpdated
                    FROM Leases
                    LIMIT 1""");

            //DocumentPermissions table
            con.createStatement().execute("""
                    SELECT
                        userID,
                        docID,
                        view,
                        editName,
                        editContents,
                        delete,
                        addComment,
                        canESign
                    FROM DocumentPermissions
                    LIMIT 1""");

            //LeasePermissions table
            con.createStatement().execute("""
                    SELECT
                        userID,
                        leaseID,
                        editName,
                        editAddress,
                        viewPaymentsPage,
                        genericTicketPerms,
                        maintTicketPerms,
                        taxTicketPerms,
                        rentTicketPerms,
                        viewTaxFinData,
                        addLeaseContract,
                        proposeLeaseChange,
                        viewLeaseContract,
                        addGenericDoc,
                        viewGenericDoc
                    FROM LeasePermissions
                    LIMIT 1""");

            //Tickets table
            con.createStatement().execute("""
                    SELECT
                        ticketID,
                        owner,
                        parentLease,
                        title,
                        description,
                        dateCreated,
                        timeModified,
                        state,
                        type
                    FROM Tickets
                    LIMIT 1""");

            //Comments table
            con.createStatement().execute("""
                    SELECT
                        commentID,
                        owner,
                        replyTo,
                        dateCreated,
                        timeModified,
                        content,
                        isParentDoc,
                        parentID
                    FROM Comments
                    LIMIT 1""");

            //Documents table
            con.createStatement().execute("""
                    SELECT
                        docID,
                        owner,
                        parentLease,
                        docType,
                        dateCreated,
                        dateModified,
                        allowUnauthView,
                        name,
                        description,
                        data blob,
                        fileSize
                    FROM Documents
                    LIMIT 1""");

            //ESignRequests table
            con.createStatement().execute("""
                    SELECT
                        parentDoc,
                        userID
                    FROM ESignRequests
                    LIMIT 1""");

            //ESignatures table
            con.createStatement().execute("""
                    SELECT
                        parentDoc,
                        signer,
                        token
                    FROM ESignatures
                    LIMIT 1""");

            //TicketAttachmentsMap table
            con.createStatement().execute("""
                    SELECT
                        docID,
                        ticketID
                    FROM TicketAttachmentsMap
                    LIMIT 1""");

            //TicketCommentsMap table
            con.createStatement().execute("""
                    SELECT
                        ticketID,
                        commentID
                    FROM TicketCommentsMap
                    LIMIT 1""");

            //CommentAttachmentsMap table
            con.createStatement().execute("""
                    SELECT
                        docID,
                        commentID
                    FROM CommentAttachmentsMap
                    LIMIT 1""");

            //RentRequests table
            con.createStatement().execute("""
                    SELECT
                        requestID,
                        leaseID,
                        dateMade,
                        dueDate,
                        amount,
                        paid
                    FROM RentRequests
                    LIMIT 1""");
        }
        catch (SQLException e)
        {
            System.out.println("DB failed validation, will clear and rebuild");
            System.out.println("If this is the first time the server has been run, this is normal as the DB has not been built yet");
            return false;
        }

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Unable to close DB during validation");
        }

        System.out.println("DB file passed validation");
        return true;
    }

    public PreparedStatement compileQuery(String query) throws SQLException
    {
        return compileQuery(query, 0);
    }

    public PreparedStatement compileQuery(String query, int flag) throws SQLException
    {
        if (query == null || query.isEmpty())
        {
            return null;
        }

        try
        {
            Connection con = establishCon();
            PreparedStatement statement = null;
            if (flag == 0)
                statement = con.prepareStatement(query);
            else
                statement = con.prepareStatement(query, flag);

            openConnections.put(statement, con);
            return statement;
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to compile SQL query: " + query);
            throw e; //Rethrow for caller to deal with
        }
    }

    public void closeConnection(PreparedStatement usedQuery)
    {
        try
        {
            Connection con = openConnections.get(usedQuery);
            if (con != null)
            {
                con.close();
                openConnections.remove(usedQuery);
            }
            else
            {
                System.out.println("WARNING: Attempted to close already closed DB connection");
            }
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Failed to close DB connection");
        }
    }

    //Used internally, do not make public as it is vulnerable to injection
    private ResultSet query(String query) throws SQLException
    {
        if (query == null || query.isEmpty())
        {
            return null;
        }

        Connection con = establishCon();
        Statement statement = con.createStatement();
        ResultSet result = null;

        try
        {
            if (statement.execute(query))
            {
                result = statement.getResultSet();
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to execute SQL query: " + query);
            throw e; //Rethrow for caller to deal with
        }

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Unable to close DB during validation");
        }

        return result;
    }
}
