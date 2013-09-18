package com.intuit.qbo.plugin;

import oauth.signpost.OAuthConsumer;

import java.io.IOException;

import javax.servlet.http.HttpSession;

public interface QBOPlugin
{
  /*
   * Allow QBO  plugin to use the session
   */
  public void setSession(HttpSession session);
  
  /*
   * Returns true when realm is in database
   */
  public boolean doesRealmExist(String realmId) throws IOException;
  
  /*
   * Returns true when passed in realm matches logged in realm (logged in means in the session)
   */
  public boolean isRealmLoggedIn(String realmId);
  
  /*
   * Logins a user in. If realm database record doesn't exist, it should create one
   * It should also create a user record if one doesn't exist.
   */
  public void login(String realmId, String userId, String firstName, String lastName, String email) throws IOException;
  
  /*
   * Saves oAuth tokens and token secret with realm. oAuth tokens are realm scoped.
   */
  public void saveoAuth(String dataSource, OAuthConsumer consumer) throws IOException;
  
  /*
   * Returns true when logged in realm has a token and secret
   */
  public boolean getHasValidOAuthConsumer();
  
  /*
   * Returns uri to app's signup page. The signup page should be static with some marketing content
   */
  public String getSignupUrl(String realmId);

  /*
   * Returns uri to app's main page. This page should be protected by an authentication filter. It is the start page
   * for the logged in user.
   */
  public String getStartPage();
}
