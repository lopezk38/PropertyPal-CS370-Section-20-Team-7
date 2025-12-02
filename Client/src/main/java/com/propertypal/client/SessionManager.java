package com.propertypal.client;

public class SessionManager
{
    // Singleton instance
    private static SessionManager instance;

    // User info
    private String username;
    private Role role;

    public enum Role
    {
        TENANT,
        LANDLORD
    }

    // Private constructor
    private SessionManager()
    {

    }

    // Get singleton instance
    public static SessionManager getInstance()
    {
        if (instance == null)
        {
            instance = new SessionManager();
        }
        return instance;
    }

    // Set user info after login
    public void login(String username, Role role)
    {
        this.username = username;
        this.role = role;
    }

    // Clear session
    public void logout()
    {
        this.username = null;
        this.role = null;
    }

    // Getters
    public String getUsername()
    {
        return username;
    }

    public Role getRole()
    {
        return role;
    }

    public boolean isTenant()
    {
        return role == Role.TENANT;
    }

    public boolean isLandlord()
    {
        return role == Role.LANDLORD;
    }
}