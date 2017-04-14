package users;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class AuthService {
	private static SecureRandom random = new SecureRandom();
	
	private static final String COOKIE_NAME = "remember-me";
    public static final String SESSION_USERNAME = "username";

    public static boolean isAuthenticated() {
        return VaadinSession.getCurrent()
                .getAttribute(SESSION_USERNAME) != null
                        || loginRememberedUser();
    }

    public static boolean login(String username, String password,
            boolean rememberMe) {
            VaadinSession.getCurrent().setAttribute(
                    SESSION_USERNAME, username);

            if (rememberMe) {
                rememberUser(username);
            }
            return true;
        }


    public static void logOut() {
        Optional<Cookie> cookie = getRememberMeCookie();
        if (cookie.isPresent()) {
            String id = cookie.get().getValue();
            //UserService.removeRememberedUser(id);
            deleteRememberMeCookie();
        }

        VaadinSession.getCurrent().close();
        Page.getCurrent().setLocation("");
    }

    private static Optional<Cookie> getRememberMeCookie() {
        Cookie[] cookies =
                VaadinService.getCurrentRequest().getCookies();
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(COOKIE_NAME))
                .findFirst();
    }
    
    public static String getRememberedUser(String id) {
    	//User.getAuthKeys();
        //return rememberedUsers.get(id);
    	return null;
    }

    public static boolean loginRememberedUser() {
        Optional<Cookie> rememberMeCookie = getRememberMeCookie();

        if (rememberMeCookie.isPresent()) {
            String id = rememberMeCookie.get().getValue();
            String username = getRememberedUser(id);

            if (username != null) {
                VaadinSession.getCurrent()
                        .setAttribute(SESSION_USERNAME, username);
                return true;
            }
        }

        return false;
    }

    public static void rememberUser(String username) {
      
        String randomId = new BigInteger(130, random).toString(32);
        Cookie cookie = new Cookie(COOKIE_NAME, randomId);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // valid for 30 days
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    private static void deleteRememberMeCookie() {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }
}
