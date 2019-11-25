package edu.vrg18.cyber_chat;

//import org.junit.jupiter.api.Test;

import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
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
    private UserDto testUser1;
    private UserDto testUser2;
    private RoomDto testRoom;

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
                        new UserDto(null, USERNAME_1, true, true,
                                FIRSTNAME_1, null, null, null, PASSWORD, null)));
        userIdListForRemoteAfterTest.add(testUser1.getId());

        testUser2 = userService.getUserByUserName(USERNAME_2)
                .orElseGet(() -> userService.createUser(
                        new UserDto(null, USERNAME_2, true, true,
                                FIRSTNAME_2, null, null, null, PASSWORD, null)));
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

        UserDto userDto = new UserDto(null, USERNAME_3, true, true, FIRSTNAME_3, null, null, null, PASSWORD, null);

        userIdListForRemoteAfterTest.add(UUID.fromString(
                given().log().body()
                        .contentType(ContentType.JSON).body(userDto)
                        .when().post(BASE_PATH + "users")
                        .then().log().body()
                        .statusCode(HttpStatus.OK.value())
                        .body("userName", equalTo(USERNAME_3))
                        .extract().jsonPath().get("id")));
    }

    @Test
    public void whenUpdateUser_thenStatus200() {

        UserDto userDto = new UserDto(testUser1.getId(), USERNAME_1, true, true,
                FIRSTNAME_1, LASTNAME_1, null, null, null, null);

        given().log().body()
                .contentType(ContentType.JSON).body(userDto)
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

        RoomDto testRoom = roomService.findAllPublicRooms().get(0);
        MessageDto messageDto = new MessageDto(null, null, testUser1, testRoom, MESSAGE_TEXT);

        messageIdListForRemoteAfterTest.add(UUID.fromString(
                given().log().body()
                        .contentType(ContentType.JSON).body(messageDto)
                        .auth().preemptive().basic(USERNAME_1, PASSWORD)
                        .when().post(BASE_PATH + "messages")
                        .then().log().body()
                        .statusCode(HttpStatus.OK.value())
                        .body("text", equalTo(MESSAGE_TEXT))
                        .extract().jsonPath().get("id")));
    }
}
