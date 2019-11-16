package edu.vrg18.cyber_chat;

//import org.junit.jupiter.api.Test;

import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
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

    private static final String BASE_PATH = "/rest/";
    private static final String USERNAME_1 = "testbot1";
    private static final String USERNAME_2 = "testbot2";
    private static final String USERNAME_3 = "testbot3";
    private static final String PASSWORD = "123456";
    private static final String FIRSTNAME_1 = "TestBot1";
    private static final String LASTNAME_1 = "ТестовыйБот";
    private static final String FIRSTNAME_2 = "TestBot2";
    private static final String FIRSTNAME_3 = "TestBot3";
    private static final String MESSAGE_TEXT = "SimbirSoft forever";

    @LocalServerPort
    private int port;

    private UserService userService;
    private MessageService messageService;
    private RoomService roomService;
    private ModelMapper roomMapper;
    private List<UUID> userIdListForRemoteAfterTest = new ArrayList<>();
    private List<UUID> messageIdListForRemoteAfterTest = new ArrayList<>();
    private User testUser1;
    private User testUser2;
    private String testEncryptedPassword1;
    private String testEncryptedPassword2;
    private Room testRoom;

    @Autowired
    private void setService(UserService userService, MessageService messageService, RoomService roomService) {
        this.userService = userService;
        this.messageService = messageService;
        this.roomService = roomService;
    }

    @Autowired
    private void setMapper(ModelMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    @Before
    public void setup() {

        RestAssured.port = port;

        testUser1 = userService.getUserByUserName(USERNAME_1)
                .orElseGet(() -> userService.createUser(
                        new User(null, USERNAME_1, null, true, true,
                                FIRSTNAME_1, null, null, null, PASSWORD)));

        testEncryptedPassword1 = testUser1.getEncryptedPassword();
        userIdListForRemoteAfterTest.add(testUser1.getId());

        testUser2 = userService.getUserByUserName(USERNAME_2)
                .orElseGet(() -> userService.createUser(
                        new User(null, USERNAME_2, null, true, true,
                                FIRSTNAME_2, null, null, null, PASSWORD)));

        testEncryptedPassword2 = testUser2.getEncryptedPassword();
        userIdListForRemoteAfterTest.add(testUser2.getId());
    }

    /**
     * The method is executed after the test/tests
     * and cleans the database from records created during tests
     */
    @After
    public void restoreDb() {

        messageIdListForRemoteAfterTest.forEach(id -> messageService.deleteMessage(id));
        userIdListForRemoteAfterTest.forEach(id -> userService.deleteUser(id));
    }

    @Test
    public void whenCreateUser_thenStatus200() {

        User user = new User(null, USERNAME_3, null, true, true, FIRSTNAME_3, null, null, null, PASSWORD);

        userIdListForRemoteAfterTest.add(UUID.fromString(
                given().log().body()
                        .contentType(ContentType.JSON).body(user)
                        .when().post(BASE_PATH + "users")
                        .then().log().body()
                        .statusCode(HttpStatus.OK.value())
                        .body("userName", equalTo(USERNAME_3))
                        .extract().jsonPath().get("id")));
    }

    @Test
    public void whenUpdateUser_thenStatus200() {

        User user = new User(testUser1.getId(), USERNAME_1, testEncryptedPassword1, true, true, FIRSTNAME_1, LASTNAME_1, null, null, null);

        given().log().body()
                .contentType(ContentType.JSON).body(user)
                .auth().preemptive().basic(USERNAME_1, PASSWORD)
                .when().put(BASE_PATH + "users")
                .then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("lastName", equalTo(LASTNAME_1));
    }

    @Test
    public void whenGetUserByName_thenStatus200() {

        given().pathParam("name", USERNAME_1)
                .when().get(BASE_PATH + "users/name/{name}")
                .then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("userName", equalTo(USERNAME_1));
    }

    @Test
    public void whenGetUserById_thenStatus200() {

        given().pathParam("id", testUser1.getId().toString())
                .when().get(BASE_PATH + "users/{id}")
                .then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("userName", equalTo(USERNAME_1));
    }

    @Test
    public void whenGetUnreadMessageByUserId_thenStatus200() {

        given().pathParam("id", testUser1.getId().toString())
                .auth().preemptive().basic(USERNAME_1, PASSWORD)
                .when().get(BASE_PATH + "messages/user/{id}")
                .then().log().body()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void whenCreateMessage_thenStatus200() {

        Room testRoom = roomMapper.map(roomService.findAllRoomsOfUserAndAllOpenRooms(testUser1).get(0), Room.class);
        Message message = new Message(null, null, testUser1, testRoom, MESSAGE_TEXT);

        messageIdListForRemoteAfterTest.add(UUID.fromString(
                given().log().body()
                        .contentType(ContentType.JSON).body(message)
                        .auth().preemptive().basic(USERNAME_1, PASSWORD)
                        .when().post(BASE_PATH + "messages")
                        .then().log().body()
                        .statusCode(HttpStatus.OK.value())
                        .body("text", equalTo(MESSAGE_TEXT))
                        .extract().jsonPath().get("id")));
    }
}
