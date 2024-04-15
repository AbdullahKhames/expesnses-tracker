package name.expenses;

public class MyLoginModule extends AppservPasswordLoginModule {

protected void authenticateUser() throws LoginException {
  _logger.info("X-TECH MyLoginModule : authenticateUser for " +
    _username);
  MyRealm realm = (MyRealm) _currentRealm;

  if (!_username.startsWith(realm.getStartWith())) {
    _logger.info("Invalid credentials.");
    throw new LoginException ("Invalid credentials.");
  }

  _logger.info("User authenticated");
  Set principals = _subject.getPrincipals();
  principals.add(new PrincipalImpl(_username));

  String grpList[] = new String[1];
  grpList[0] = "User";
  this.commitUserAuthentication(grpList);
}

}