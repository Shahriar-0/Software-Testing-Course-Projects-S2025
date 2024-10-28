package mizdooni.controllers;

import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ServiceUtils;
import mizdooni.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.MockitoJUnit;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private User mockUser;

    @Before
    public void setUp() {
        Address address = new Address("Country", "City", null);
        mockUser = new User("testUser", "testPass", "test@example.com", address, User.Role.client);
    }

    // Helper method to access private fields
    private Object getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    @Test
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
    public void testUserNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.user());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
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
    public void testLoginInvalidCredentials() {
        when(userService.login("testUser", "wrongPass")).thenReturn(false);

        Map<String, String> params = new HashMap<>();
        params.put("username", "testUser");
        params.put("password", "wrongPass");

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.login(params));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("invalid username or password", exception.getMessage());
    }

    @Test
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
    public void testSignupInvalidParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        when(userService.logout()).thenReturn(true);

        Response response = authenticationController.logout();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("logout successful", getField(response, "message"));
    }

    @Test
    public void testLogoutNoUser() {
        doReturn(false).when(userService).logout();

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.logout());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    public void testValidateUsernameAvailable() throws Exception {
        when(ServiceUtils.validateUsername("newUser")).thenReturn(true);
        when(userService.usernameExists("newUser")).thenReturn(false);

        Response response = authenticationController.validateUsername("newUser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("username is available", getField(response, "message"));
    }

    @Test
    public void testValidateUsernameExists() {
        when(ServiceUtils.validateUsername("existingUser")).thenReturn(true);
        when(userService.usernameExists("existingUser")).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateUsername("existingUser"));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("username already exists", exception.getMessage());
    }

    @Test
    public void testValidateEmailAvailable() throws Exception {
        when(ServiceUtils.validateEmail("email@example.com")).thenReturn(true);
        when(userService.emailExists("email@example.com")).thenReturn(false);

        Response response = authenticationController.validateEmail("email@example.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, getField(response, "status"));
        assertTrue((Boolean) getField(response, "success"));
        assertEquals("email not registered", getField(response, "message"));
    }

    @Test
    public void testValidateEmailExists() {
        when(ServiceUtils.validateEmail("registered@example.com")).thenReturn(true);
        when(userService.emailExists("registered@example.com")).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateEmail("registered@example.com"));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("email already registered", exception.getMessage());
    }
}
