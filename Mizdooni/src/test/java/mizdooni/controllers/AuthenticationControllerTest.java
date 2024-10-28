package mizdooni.controllers;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private User mockUser;

    @Before
    public void setUp() {
        Address address = new Address("Country", "City", null);
        mockUser =
            new User(
                "testUser",
                "testPass",
                "test@example.com",
                address,
                User.Role.client
            );
    }

    // Helper method to access private fields
    private Object getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    @Test
    @DisplayName("Test User")
    public void testUserLoggedIn() throws Exception {
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.user();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("current user", getField(response, "message"));
        assertEquals(mockUser, getField(response, "data"));
    }

    @Test
    @DisplayName("Test User Not Logged In")
    public void testUserNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.user()
        );
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    @DisplayName("Test Login Success")
    public void testLoginSuccess() throws Exception {
        when(userService.login("testUser", "testPass")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Map<String, String> params = new HashMap<>();
        params.put("username", "testUser");
        params.put("password", "testPass");

        Response response = authenticationController.login(params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("login successful", getField(response, "message"));
        assertEquals(mockUser, getField(response, "data"));
    }

    @Test
    @DisplayName("Test Login Invalid Credentials")
    public void testLoginInvalidCredentials() {
        when(userService.login("testUser", "wrongPass")).thenReturn(false);

        Map<String, String> params = new HashMap<>();
        params.put("username", "testUser");
        params.put("password", "wrongPass");

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.login(params)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("Test Signup Success")
    public void testSignupSuccess() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");
        params.put("password", "password123");
        params.put("email", "email@example.com");
        params.put("role", "client");

        Map<String, String> address = new HashMap<>();
        address.put("country", "Country");
        address.put("city", "City");
        params.put("address", address);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.signup(params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("signup successful", getField(response, "message"));
        assertEquals(mockUser, getField(response, "data"));
    }

    @Test
    @DisplayName("Test Signup Invalid Params")
    public void testSignupInvalidParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.signup(params)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test Signup Invalid Params Type")
    public void testSignupInvalidParamsType() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");
        params.put("password", 1234);
        params.put("email", "email@example.com");
        params.put("role", "client");

        Map<String, String> address = new HashMap<>();
        address.put("country", "Country");
        address.put("city", "City");
        params.put("address", address);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.signup(params)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test Logout Success")
    public void testLogoutSuccess() throws Exception {
        when(userService.logout()).thenReturn(true);

        Response response = authenticationController.logout();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("logout successful", getField(response, "message"));
    }

    @Test
    @DisplayName("Test Logout No User")
    public void testLogoutNoUser() {
        doReturn(false).when(userService).logout();

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.logout()
        );
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    @DisplayName("Test Validate Username Success")
    public void testValidateUsernameAvailable() throws Exception {
        when(userService.usernameExists("newUser")).thenReturn(false);

        Response response = authenticationController.validateUsername("newUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("username is available", getField(response, "message"));
    }

    @Test
    @DisplayName("Test Validate Username Exists")
    public void testValidateUsernameExists() {
        when(userService.usernameExists("existingUser")).thenReturn(true);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.validateUsername("existingUser")
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("username already exists", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "#username", "username123!", "user@name123!", "use12rname3@!#" })
    @DisplayName("Test Invalid Username Format")
    public void testInvalidUsernameFormat(String username) {
        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.validateUsername(username)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid username format", exception.getMessage());
    }

    @Test
    @DisplayName("Test Validate Email Success")
    public void testValidateEmailAvailable() throws Exception {
        when(userService.emailExists("email@example.com")).thenReturn(false);

        Response response = authenticationController.validateEmail("email@example.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("email not registered", getField(response, "message"));
    }

    @Test
    @DisplayName("Test Validate Email Exists")
    public void testValidateEmailExists() {
        when(userService.emailExists("registered@example.com")).thenReturn(true);

        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.validateEmail("registered@example.com")
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("email already registered", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "#email", "email@example", "email@.com", "email@.com." })
    @DisplayName("Test Invalid Email Format")
    public void testInvalidEmailFormat(String email) {
        ResponseException exception = assertThrows(
            ResponseException.class,
            () -> authenticationController.validateEmail(email)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid email format", exception.getMessage());
    }
}
