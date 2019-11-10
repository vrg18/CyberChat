package edu.vrg18.cyber_chat;

//import org.junit.jupiter.api.Test;

import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApplicationTests {

    private static final String API_BASE_PATH = "/rest/";
    private static final String API_USERNAME_1 = "testbot1";
    private static final String API_USERNAME_2 = "testbot2";
    private static final String API_PASSWORD = "123456";
    private static final String API_FIRSTNAME_1 = "TestBot1";
    private static final String API_FIRSTNAME_2 = "TestBot2";

    @LocalServerPort
    private int port;

    private UserService userService;
    private MessageService messageService;
    private List<UUID> userIdListForRemoteAfterTest = new ArrayList<>();
    private List<UUID> messageIdListForRemoteAfterTest = new ArrayList<>();
    private UUID testUserId;
    private String testEncryptedPassword;

    @Autowired
    private void setService(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Before
    public void setup() {

        RestAssured.port = port;

        User testUser = userService.getUserByUserName(API_USERNAME_1)
                .orElseGet(() -> userService.createUser(
                        new User(null, API_USERNAME_1, null, true, true,
                                API_FIRSTNAME_1, null, null, null, API_PASSWORD)));

        testUserId = testUser.getId();
        testEncryptedPassword = testUser.getEncryptedPassword();
        userIdListForRemoteAfterTest.add(testUserId);
    }

    /**
     * The method is executed after the test/tests
     * and cleans the database from records created during tests
     */
    @After
    public void restoreDb() {

        userIdListForRemoteAfterTest.forEach(id -> userService.deleteUser(id));
        messageIdListForRemoteAfterTest.forEach(id -> messageService.deleteMessage(id));
    }

    @Test
    public void whenCreateUser_thenStatus200() {

        User user = new User(null, API_USERNAME_2, null, true, true, API_FIRSTNAME_2, null, null, null, API_PASSWORD);

        userIdListForRemoteAfterTest.add(UUID.fromString(
                given().log().body()
                        .contentType(ContentType.JSON).body(user)
                        .when().post(API_BASE_PATH + "users")
                        .then().log().body()
                        .statusCode(HttpStatus.OK.value())
                        .body("userName", equalTo(API_USERNAME_2))
                        .extract().jsonPath().get("id")));
    }

    @Test
    public void whenUpdateUser_thenStatus200() {

        User user = new User(testUserId, API_USERNAME_1, testEncryptedPassword, true, true, API_FIRSTNAME_2, null, null, null, null);

        given().log().body()
                .contentType(ContentType.JSON).body(user)
                .auth().preemptive().basic(API_USERNAME_1, API_PASSWORD)
                .when().put(API_BASE_PATH + "users")
                .then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo(API_FIRSTNAME_2));
    }

    @Test
    public void whenGetUserByName_thenStatus200() {

        given().pathParam("name", API_USERNAME_1)
                .when().get(API_BASE_PATH + "users/name/{name}")
                .then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("userName", equalTo(API_USERNAME_1));
    }

    @Test
    public void whenGetUserById_thenStatus200() {

        given().pathParam("id", testUserId.toString())
                .when().get(API_BASE_PATH + "users/{id}")
                .then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("userName", equalTo(API_USERNAME_1));
    }
}
