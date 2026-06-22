package study.ywork.springldap.constants;

public class LdapAuthConstant {
    /* Page title constants */
    public static final String PAGE_TITLE = "pageTitle";
    public static final String TITLE_HOME_PAGE = "Home";
    public static final String TITLE_PRIVATE_PAGE = "Private";
    public static final String TITLE_LOGIN_PAGE = "Login";

    /* LDAP Auth structure constants */
    public static final String LDAP_USER_SEARCH = "ou=users";
    public static final String LDAP_GROUP_SEARCH = "ou=roles";
    public static final String LDAP_USER_DN_PATTERN = "uid={0},ou=users";

    private LdapAuthConstant() {
    }
}
