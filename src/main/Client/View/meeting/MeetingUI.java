package main.Client.View.meeting;
//
//import common.meeting.ChatMeeting;
//import common.meeting.MeetingService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

//import main.Client.DTO.Participant;
import main.Client.ClientMain;
import main.Client.Controller.MeetingChatController;
import main.Client.Controller.MeetingController;
import main.util.ImageUtil;
import main.util.Session;
//import main.Client.Controller.MeetingService;
import shared.DTO.ChatMeeting;
import shared.DTO.Meeting_participantDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingUI extends StackPane {
    private HBox rootLayout;            // HBox chia 7:3
    private VBox videoContainer;   // vùng video
    private VBox rightContainer;         // vùng chat
    private List<VideoTile> tiles;
    private VideoCallPane videoCallPane;
    private Meeting_participantDTO currentUser;
    private String lastSender = null; // sender cua tin nhan truoc do
    private String roomId;
    private TextField messageInput;
    private Button sendBtn;
    private VBox messageList; // 👈 đưa lên field

    private MeetingController meetingController;

    private MeetingChatController chatController;
    private ObservableList<Meeting_participantDTO> participantsList;
    private final Map<String, String> userNameCache = new HashMap<>();


    private StackPane contentPane;

    private String fakeRoomId;
    private ImageView micImage;
    private Button micBtn;
    private ImageView camImage;
    private Label micLabel;
    private Button camBtn;
    private Label camLabel;
    private Image micOff;
    private Image micOn;
    private Image camOff;
    private Image camOn;
    private StackPane emptyStatePane;

    public MeetingUI(StackPane contentPane) {
        this.contentPane = contentPane;
        participantsList = FXCollections.observableArrayList();
        initUI();
    }

    private void initUI() {
        initEmptyState();
        messageList = new VBox(8);
        messageList.setPadding(new Insets(5));

        ScrollPane scrollPane = new ScrollPane(messageList);
        scrollPane.setFitToWidth(true);

        Session session = Session.getInstance();
        if (!session.isLoggedIn()) {
            System.out.println("User not logged in");
        }

        String userId = session.getUserIdHex();
        chatController = new MeetingChatController(fakeRoomId, userId);
        chatController.setUiListener(new MeetingChatController.UiListener() {
            @Override
            public void onMessageReceived(ChatMeeting msg) {
//                addMessage(msg.getSender(), msg.getContent());
//                if (msg.getSender().equals(Session.getInstance().getUserIdHex())) {
//                    return; // tránh duplicate
//                }
                addMessage(msg.getSender(), msg.getContent());
            }

            @Override
            public void onSystemMessage(String text) {
                addSystemMessage(text);
            }
        });
        try {
            chatController.connect();
            System.out.println("RMI meeting chat connected");
        } catch (Exception e) {
            e.printStackTrace();
            addSystemMessage("Cannot connect meeting chat");
        }
        //
        //currentUser = getCurrentUser();



        rootLayout = new HBox();
        rootLayout.setSpacing(10);   // nếu bạn muốn khoảng giữa 2 panel
        rootLayout.setPadding(Insets.EMPTY); // xóa viền trắng

        // Video zone (7 phần)
        videoContainer = new VBox(10);
        videoContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15;");
        videoContainer.setPadding(new Insets(10));
        HBox.setHgrow(videoContainer, Priority.ALWAYS);  // toan bo videoContainer chiem toan bo phan ben trai theo chieu ngang

        videoCallPane = new VideoCallPane();
        tiles = new ArrayList<>();

        // ÉP videoCallPane fill 100% bên trong videoContainer
        videoCallPane.prefWidthProperty().bind(videoContainer.widthProperty());
        videoCallPane.prefHeightProperty().bind(videoContainer.heightProperty());
        videoCallPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

//        videoCallPane.updateLayout(tiles);

        // Cac nut dieu khien
        HBox controlBar = new HBox(20);
        controlBar.setPadding(new Insets(10));
        controlBar.setAlignment(Pos.CENTER);
        controlBar.setStyle("-fx-background-color: #fff;");

        VBox micBox = createMicControl();
        VBox cameraBox = createCameraControl();
        VBox raiseBox = styleControlBox("/images/raise_off.png", "Raise");

        VBox reactBox = new VBox(5);
        reactBox.setAlignment(Pos.CENTER);
        reactBox.setPadding(new Insets(5));
        reactBox.setStyle("-fx-background-color: #fff;");
        ImageView reactImage = new ImageView(new Image(getClass().getResource("/images/like.png").toExternalForm()));
        reactImage.setFitHeight(28);
        reactImage.setFitWidth(28);
        Button reactBtn = new Button();
        reactBtn.setGraphic(reactImage);
        reactBtn.setStyle("-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);");
        reactBtn.setPrefSize(50, 50);
        reactBtn.setMinSize(50, 50);
        reactBtn.setMaxSize(50, 50);
        Label reactLabel = new Label("Reaction");
        reactLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        reactLabel.setStyle("-fx-text-fill: #000");
        reactBox.getChildren().addAll(reactBtn, reactLabel);

        VBox leaveBox = new VBox(5);
        leaveBox.setAlignment(Pos.CENTER);
        leaveBox.setPadding(new Insets(5));
        leaveBox.setStyle("-fx-background-color: #fff;");
        ImageView leaveImage = new ImageView(new Image(getClass().getResource("/images/leave.png").toExternalForm()));
        leaveImage.setFitHeight(28);
        leaveImage.setFitWidth(28);

        Button leaveBtn = new Button();
        leaveBtn.setGraphic(leaveImage);
        leaveBtn.setStyle("-fx-background-color: #ef233c; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);");
        leaveBtn.setPrefSize(50, 50);
        leaveBtn.setMinSize(50, 50);
        leaveBtn.setMaxSize(50, 50);

        leaveBtn.setOnAction(e -> meetingController.leaveMeeting());

        Label leaveLabel = new Label("Leave");
        leaveLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        leaveLabel.setStyle("-fx-text-fill: #000");
        leaveBox.getChildren().addAll(leaveBtn, leaveLabel);

        controlBar.getChildren().addAll(micBox, cameraBox, raiseBox, reactBox, leaveBox);
        controlBar.setAlignment(Pos.CENTER);


        videoContainer.getChildren().addAll(videoCallPane, controlBar);

        // RIGHT PART (3 phan)
        rightContainer = new VBox(15);

        // Danh sach nguoi tham gia cuoc hop
        VBox listContainer = new VBox(5);
        listContainer.setPadding(new Insets(10));
        VBox.setMargin(listContainer, new Insets(15, 0, 0, 0));

        // So luong nguoi tham gia
        HBox numberParticipants = new HBox();
        numberParticipants.setPadding(new Insets(10));
        numberParticipants.setStyle("-fx-background-color: #F6EBFF; -fx-background-radius: 15; -fx-border-radius: 15;");
        numberParticipants.setAlignment(Pos.CENTER_LEFT);

        Label numberLabel = new Label("Participants");
        numberLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        numberLabel.setStyle("-fx-text-fill: #9d4edd");
        HBox.setMargin(numberLabel, new Insets(0, 10, 0, 10));

        Label numberOfParticipant = new Label();
        numberOfParticipant.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        numberOfParticipant.setStyle("-fx-text-fill: #9d4edd");

        ImageView add_participant_image = new ImageView(new Image(getClass().getResource("/images/add_participant.png").toExternalForm()));
        add_participant_image.setFitWidth(20);
        add_participant_image.setFitHeight(20);
        Button addParticipantButton = new Button();
        addParticipantButton.setGraphic(add_participant_image);
        addParticipantButton.setPrefSize(24, 24);
        addParticipantButton.setMinSize(24, 24);
        addParticipantButton.setMaxSize(24, 24);
        addParticipantButton.setStyle("-fx-background-color: #F6EBFF");
        HBox.setMargin(addParticipantButton, new Insets(0, 0, 0, 150));

        addParticipantButton.setOnMouseClicked(e -> {

        });

            // Hien thi danh sach nguoi tham gia
            VBox listParticipants = new VBox(10);
            listParticipants.setStyle("-fx-background-color: #fff");

            participants.addListener((ListChangeListener<Meeting_participantDTO>) change -> {

                // ====== BUILD / UPDATE USER NAME CACHE ======
                userNameCache.clear();
                for (Meeting_participantDTO p : participants) {
                    userNameCache.put(p.getUserId(), p.getFullName());
                }
                // ===========================================

                listParticipants.getChildren().clear();
                for (Meeting_participantDTO p : participants) {
                    HBox row = new HBox(10);
                    row.setPadding(new Insets(5));

                    ImageView avatar = new ImageView((ImageUtil.loadAvatarSafe(p.getAvatar())));
                    avatar.setFitWidth(40);
                    avatar.setFitHeight(40);
                    avatar.setClip(new Circle(20, 20, 20));

                    Label nameLabel = new Label(p.getUsername());
                    nameLabel.setFont(Font.font("Popppins", FontWeight.BOLD, 13));
                    Label roleLabel = new Label();
                    roleLabel.setFont(Font.font("Popppins", FontWeight.BOLD, 13));

                    String role = p.getRole();

                    if ("admin".equalsIgnoreCase(role)) {
                        roleLabel.setText("(admin)");
                    }
                    else if ("host".equalsIgnoreCase(role)) {
                        roleLabel.setText("(host)");
                    }
                    else {
                        roleLabel.setVisible(false);
                        roleLabel.setManaged(false); // không chiếm chỗ
                    }

                    // Đẩy các button sang lề phải
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // button Mic - Camera
                    Button micButton = new Button();
                    Button cameraButton = new Button();

                    // Dong bo icon
                    updateMicIcon(micButton, !p.isMuted());
                    updateCameraIcon(cameraButton, p.isCameraOn());

                    boolean canControl = canControl(currentUser, p);

                    // Click dieu khien mic / camera cua cac thanh vien
                    // == Mic ==
                    micButton.setOnMouseClicked(e -> {
                        if (!canControl) {
                            Tooltip noPermissionTooltip = new Tooltip("Only the host or admin has permission");
                            Tooltip.install(micButton, noPermissionTooltip);
                            noPermissionTooltip.show(micButton, e.getSceneX(), e.getSceneY());
                        }
                        else {
                            meetingController.setMicUser(p.getUserId());
                        }
                    });

                    // == Camera ==
                    cameraButton.setOnMouseClicked(e -> {
                        if (!canControl) {
                            Tooltip noPermissionTooltip = new Tooltip("Only the host or admin has permission");
                            Tooltip.install(cameraButton, noPermissionTooltip);
                            noPermissionTooltip.show(cameraButton, e.getSceneX(), e.getSceneY());
                        }
                        else {
                          meetingController.setCameraUser(p.getUserId());
                        }
                    });

                    // == Kick == Chi hien thi neu la admin hoac host
                    Button kickButton = new Button();
                    ImageView kickImageView = new ImageView(new Image(getClass().getResource("/images/meeting/kick.png").toExternalForm()));
                    kickImageView.setFitHeight(15);
                    kickImageView.setFitWidth(15);
                    kickButton.setGraphic(kickImageView);
                    kickButton.setStyle("-fx-background-color: #fff");
                    kickButton.setPrefSize(18, 18);
                    kickButton.setMinSize(18, 18);
                    kickButton.setMaxSize(18, 18);

                    boolean canKick = canKick(currentUser);
                    if (!canKick) {
                        kickButton.setVisible(false);
                        kickButton.setManaged(false); // xoa button ra khoi HBox => khong anh huong den layout cua HBox
                    }

                    kickButton.setOnMouseClicked(e -> {
                        meetingController.kickUser(p.getUserId());
                    });

                    row.getChildren().addAll(avatar, nameLabel, roleLabel, spacer, micButton, cameraButton, kickButton);
                    row.setAlignment(Pos.CENTER_LEFT);
                    listParticipants.getChildren().add(row);
                }

                // cap nhat so luong nguoi tham gia
                numberOfParticipant.setText(String.valueOf(participants.size()));
            });

            // Lay danh sach nguoi tham gia
            getParticipantsList();

            ScrollPane scrollPaneListParticipants = new ScrollPane(listParticipants);
            scrollPaneListParticipants.setFitToWidth(true);
            scrollPaneListParticipants.setStyle("-fx-background-color: #fff");

            numberParticipants.getChildren().addAll(numberLabel, numberOfParticipant, addParticipantButton);
            listContainer.getChildren().addAll(numberParticipants, scrollPaneListParticipants);

        // Chatting trong cuoc hop
        VBox chatContainer = new VBox(5);

        // Tieu de
        HBox messageHeader = new HBox();
        messageHeader.setPadding(new Insets(10));
        messageHeader.setStyle("-fx-background-color: #F6EBFF; -fx-background-radius: 15; -fx-border-radius: 15;");
        messageHeader.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(messageHeader, new Insets(0, 10, 0, 10));

        Label title = new Label("Messages");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #9d4edd");
        HBox.setMargin(title, new Insets(0, 10, 0, 10));

        ImageView chatIcon = new ImageView(new Image(getClass().getResource("/images/meeting/message.png").toExternalForm()));
        chatIcon.setFitWidth(20);
        chatIcon.setFitHeight(20);

        // Đẩy icon sang lề phải
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        messageHeader.getChildren().addAll(title, spacer2, chatIcon);

        // Khu vuc hien thi tin nhan
        messageList.setStyle("-fx-background-color: #fff");

        ScrollPane messageScroll = new ScrollPane(messageList);
        messageScroll.setFitToWidth(true);
        messageScroll.setPrefHeight(220);

        messageScroll.setStyle("-fx-background-color: #fff;");

        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


        // auto scroll xuong cuoi
        messageList.heightProperty().addListener(
                (obs, oldVal, newVal) -> messageScroll.setVvalue(1.0)
        );
        chatContainer.setStyle("-fx-background-color: white;");
        rightContainer.setStyle("-fx-background-color: white;");

        messageScroll.lookup(".viewport");
        Platform.runLater(() -> {
            Node viewport = messageScroll.lookup(".viewport");
            if (viewport != null) {
                viewport.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 15;"
                );
            }
        });

        // bo góc cho ScrollPane
        messageScroll.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;"
        );

        messageList.setStyle("-fx-background-color: white;");
//        messageList.setMinHeight(220);
        messageList.prefWidthProperty().bind(messageScroll.widthProperty());



        // Gui tin nhan
        ImageView fileImageView = new ImageView(new Image(getClass().getResource("/images/meeting/file.png").toExternalForm()));
        fileImageView.setFitWidth(15);
        fileImageView.setFitHeight(15);
        Button addFileBtn = new Button();
        addFileBtn.setGraphic(fileImageView);
        addFileBtn.setMaxSize(18, 18);
        addFileBtn.setPrefSize(18, 18);
        addFileBtn.setMinSize(18, 18);
        addFileBtn.setStyle("-fx-background-color: #fff; -fx-border-color: #fff;");

        addFileBtn.setOnMouseClicked(e -> {

        });


        messageInput = new TextField();
        messageInput.setPromptText("Write message here...");
        messageInput.setPrefHeight(38);
        messageInput.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: #fff;");

        ImageView sendImageView = new ImageView(new Image(getClass().getResource("/images/meeting/send.png").toExternalForm()));
        sendImageView.setFitHeight(18);
        sendImageView.setFitWidth(18);
        sendBtn = new Button();
        sendBtn.setGraphic(sendImageView);
        sendBtn.setMinSize(18, 18);
        sendBtn.setMaxSize(18, 18);
        sendBtn.setPrefSize(18, 18);
        sendBtn.setStyle(
                "-fx-background-color: #fff;"
        );
        sendBtn.setPrefSize(38, 38);
        sendBtn.setOnAction(e -> {
            System.out.println("[UI] Send button clicked");

            String text = messageInput.getText().trim();
            if (text.isEmpty()) return;
            // Optimistic UI
            //addMessage(userId, text);
//            addMessage(
//                    Session.getInstance().getUserIdHex(),
//                    text
//            );
            try {
                chatController.sendMessage(text);
                messageInput.clear();
                System.out.println("[UI] Message sent");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ENTER = SEND
        messageInput.setOnAction(e -> sendBtn.fire());
        HBox.setMargin(sendBtn, new Insets(0, 10, 0, 0));

        HBox inputArea = new HBox(8, addFileBtn, messageInput, sendBtn);
        inputArea.setPadding(new Insets(5, 8, 5, 8));
        inputArea.setStyle("-fx-background-color: #fff; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);");
        VBox.setMargin(inputArea, new Insets(0, 10, 10, 10));
        inputArea.setAlignment(Pos.CENTER);
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        chatContainer.getChildren().addAll(messageHeader, messageScroll, inputArea);
        VBox.setVgrow(messageScroll, Priority.ALWAYS);

        rightContainer.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-border-radius: 10;");
        listContainer.prefHeightProperty().bind(rightContainer.heightProperty().multiply(0.4));
        chatContainer.prefHeightProperty().bind(rightContainer.heightProperty().multiply(0.6));
        rightContainer.getChildren().addAll(listContainer, chatContainer);


        videoCallPane.prefWidthProperty().bind(rootLayout.widthProperty().multiply(0.7));
        rightContainer.prefWidthProperty().bind(rootLayout.widthProperty().multiply(0.3));
        rootLayout.getChildren().addAll(videoContainer, rightContainer);

        this.getChildren().addAll(emptyStatePane, rootLayout);

        addSystemMessage("Welcome to meeting chat");

    }

    // Dieu khien cac button trong video call
    private VBox styleControlBox(String iconPath, String label) {
        Image iconOff = new Image(getClass().getResource(iconPath).toExternalForm());
        Image iconOn = new Image(getClass().getResource(iconPath.replace("_off.png", "_on.png")).toExternalForm());
        ImageView imageView = new ImageView(iconOff);
        imageView.setFitWidth(28);
        imageView.setFitHeight(28);

        Button buttonItem = new Button();
        buttonItem.setGraphic(imageView);
        buttonItem.setPrefSize(50, 50);
        buttonItem.setMinSize(50, 50);
        buttonItem.setMaxSize(50, 50);
        buttonItem.setStyle("-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);");

        Label labelItem = new Label(label);
        labelItem.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        labelItem.setStyle("-fx-text-fill: #000;");

        // Style VBox
        VBox box = new VBox(5, buttonItem, labelItem);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5));
        box.setStyle("-fx-background-color: #fff;");

        // ---- STATE ----
        final boolean[] isOn = {false};  // mặc định off

        // ---- Toggle style ----
        buttonItem.setOnMouseClicked(e -> {
            isOn[0] = !isOn[0];   // Đổi trạng thái

            if (isOn[0]) {
                // TURN ON
                imageView.setImage(iconOn);
                labelItem.setStyle("-fx-text-fill: #6A00F4;");
                buttonItem.setStyle(
                        "-fx-background-color: #E6D4FF; -fx-border-radius: 15; -fx-background-radius: 15;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.20), 8, 0.3, 0, 1);"
                );
            } else {
                // TURN OFF
                imageView.setImage(iconOff);
                labelItem.setStyle("-fx-text-fill: #000;");
                buttonItem.setStyle(
                        "-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);"
                );
            }
        });
        return box;
    }

    private VBox createMicControl() {
        micOff = new Image(getClass().getResource("/images/mic_off.png").toExternalForm());
        micOn  = new Image(getClass().getResource("/images/mic_on.png").toExternalForm());

        micImage = new ImageView(micOff);
        micImage.setFitWidth(28);
        micImage.setFitHeight(28);

        micBtn = new Button();
        micBtn.setGraphic(micImage);
        micBtn.setPrefSize(50, 50);
        micBtn.setStyle("-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);");

        micLabel = new Label("Microphone");
        micLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 13));

        VBox box = new VBox(5, micBtn, micLabel);
        box.setAlignment(Pos.CENTER);

        final boolean[] isOn = {false};

        micBtn.setOnAction(e -> {
            if (meetingController != null) {
                meetingController.setMicUser(
                        Session.getInstance().getUserIdHex()
                );
            }
        });

        return box;
    }

    private VBox createCameraControl() {
        camOff = new Image(getClass().getResource("/images/camera_off.png").toExternalForm());
        camOn  = new Image(getClass().getResource("/images/camera_on.png").toExternalForm());

        camImage = new ImageView(camOff);
        camImage.setFitWidth(28);
        camImage.setFitHeight(28);

        camBtn = new Button();
        camBtn.setGraphic(camImage);
        camBtn.setPrefSize(50, 50);
        camBtn.setStyle("-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);");

        camLabel = new Label("Camera");
        camLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 13));

        VBox box = new VBox(5, camBtn, camLabel);
        box.setAlignment(Pos.CENTER);

        final boolean[] isOn = {false};

        camBtn.setOnAction(e -> {
            if (meetingController != null) {
                meetingController.setCameraUser(
                        Session.getInstance().getUserIdHex()
                );
            }
            // Bật/tắt camera trực tiếp trên VideoTile của user hiện tại
            VideoTile myTile = null;
            for (VideoTile tile : tiles) {
                if (tile.getUserId().equals(Session.getInstance().getUserIdHex())) {
                    myTile = tile;
                    break;
                }
            }
            if (myTile != null) {
                myTile.setCameraOn(isOn[0]);
                videoCallPane.updateLayout(tiles);
            }
        });

        return box;
    }

    private void initEmptyState() {
        Label emptyLabel = new Label("Join a meeting to get started!");
        emptyLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        emptyLabel.setStyle("-fx-text-fill: #999;");

        emptyStatePane = new StackPane(emptyLabel);
        emptyStatePane.setAlignment(Pos.CENTER);
        emptyStatePane.setVisible(false);
    }

    public void setCurrentUser(Meeting_participantDTO user) {
        this.currentUser = user;
    }

    private Meeting_participantDTO getCurrentUser() {
        return currentUser;
    }

    // cap nhat giao dien realtime khi co nguoi dung join hoac bi kick
    private ObservableList<Meeting_participantDTO> participants
            = FXCollections.observableArrayList();

    public ObservableList<Meeting_participantDTO> getParticipantsList() {
        return participants;
    }


    // Kiem tra nguoi dung co quyen dieu khien mic, camera va kick nguoi tham gia
    private boolean canControl(Meeting_participantDTO currentUser, Meeting_participantDTO normalParticipant) {
        if (currentUser == null || normalParticipant == null) {
            return false;
        }

        String role = currentUser.getRole(); // admin - host - participant

        return "admin".equalsIgnoreCase(role)
                || "host".equalsIgnoreCase(role)
                || currentUser.getUserId().equals(normalParticipant.getUserId()); // co the bat/tat chinh minh
    }

    // Kiem tra nguoi dung co quyen kick thanh vien cuoc hop
    private boolean canKick(Meeting_participantDTO currentUser) {
        if (currentUser == null) {
            return false;
        } else {
            String role = currentUser.getRole();
            return "admin".equalsIgnoreCase(role) || "host".equalsIgnoreCase(role);
        }
    }

    // Dieu khien mic cua cac thanh vien - admin/host
    private void updateMicIcon(Button btn, boolean isOn) {
        String iconList = isOn ? "/images/meeting/talking.png" : "/images/meeting/mute.png";
        micLabel.setStyle(isOn ? "-fx-text-fill: #6A00F4;" : "-fx-text-fill: #000;");
        // Thay doi icon cho button personal
        if (isOn) {
            micBtn.setGraphic(new ImageView(micOn));
        } else {
            micBtn.setGraphic(new ImageView(micOff));
        }
        ImageView imageView = new ImageView(new Image(getClass().getResource(iconList).toExternalForm()));
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);
        btn.setGraphic(imageView);
        btn.setPrefSize(18, 18);
        btn.setMinSize(18, 18);
        btn.setMaxSize(18, 18);
        btn.setStyle(isOn
                ? "-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.20), 8, 0.3, 0, 1);"
                : "-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);"
        );
    }

    // Dieu khien camera cua cac thanh vien
    private void updateCameraIcon(Button btn, boolean isOn) {
        String iconList = isOn ? "/images/meeting/camera_on.png" : "/images/meeting/camera_off.png";
        camLabel.setStyle(isOn ? "-fx-text-fill: #6A00F4;" : "-fx-text-fill: #000;");
        // Thay doi icon cho button personal
        if (isOn) {
            camBtn.setGraphic(new ImageView(camOn));
        } else {
            camBtn.setGraphic(new ImageView(camOff));
        }
        ImageView imageView = new ImageView(new Image(getClass().getResource(iconList).toExternalForm()));
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);
        btn.setGraphic(imageView);
        btn.setPrefSize(18, 18);
        btn.setMinSize(18, 18);
        btn.setMaxSize(18, 18);
//        btn.setStyle("-fx-background-color: #fff;");
        btn.setStyle(isOn
                ? "-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.20), 8, 0.3, 0, 1);"
                : "-fx-background-color: #F6EBFF; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 1);"
        );
    }

    // Thie ke tin nhan kieu bubble
    private HBox createMessageRow(
            String sender,
            String content,
            String time,
            boolean isMine,
            String previousSender
    ) {
        boolean showName = !sender.equals(previousSender);

        ImageView avatar = new ImageView(
                new Image(getClass().getResource(
                        isMine ? "/images/avatar.jpg" : "/images/avatar4.jpg"
                ).toExternalForm())
        );
        avatar.setFitWidth(32);
        avatar.setFitHeight(32);
        avatar.setClip(new Circle(16, 16, 16));

        Label nameLabel = new Label(sender);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #000; -fx-font-weight: bold;");
        nameLabel.setVisible(showName);
        nameLabel.setManaged(showName);

        Label msgLabel = new Label(content);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(240);
        msgLabel.setPadding(new Insets(8));
        msgLabel.setStyle(
                "-fx-background-radius: 12;" +
                        (isMine
                                ? "-fx-background-color: #D9FDD3;"
                                : "-fx-background-color: #F1F1F1;")
        );

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");

        VBox bubbleBox;

        if (isMine) {
            // Name: nằm trên bubble, căn phải
            nameLabel.setAlignment(Pos.CENTER_RIGHT);
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            bubbleBox = new VBox(2, nameLabel, msgLabel, timeLabel);
            bubbleBox.setAlignment(Pos.TOP_RIGHT); // cac thanh phan trong VBOX se can phai
        } else {
            bubbleBox = new VBox(2, nameLabel, msgLabel, timeLabel);
        }

        HBox row = new HBox(8);
        row.setPadding(new Insets(5));

        // Vi tri tin nhan hien thi doi voi ca nhan va nhung nguoi khac
        if (isMine) {
            row.setAlignment(Pos.TOP_RIGHT);
            row.getChildren().add(bubbleBox);
        } else {
            row.setAlignment(Pos.TOP_LEFT);
            row.getChildren().addAll(avatar, bubbleBox);
        }
        return row;
    }

    public List<VideoTile> getTiles() {
        return tiles;
    }

    public VideoCallPane getVideoCallPane() {
        return videoCallPane;
    }

    public void clearMessages() {
        messageList.getChildren().clear();
        lastSender = null;
    }
    public void addMessage(String sender, String content) {
        String displayName;
        boolean isMine = sender.equals(Session.getInstance().getUserIdHex());

//        if (sender.equals(Session.getInstance().getUserIdHex())) {
//            displayName = "You";
//        } else {
//            // tìm participant theo userId
//            Meeting_participantDTO p = findParticipantByUserId(sender);
//            displayName = (p != null) ? p.getFullName() : sender;
//        }
        if (isMine) {
            displayName = "You";
        } else {
            displayName = userNameCache.getOrDefault(sender, "Unknown");
        }


        String time = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        HBox row = createMessageRow(
                displayName,
                content,
                time,
                isMine,
                lastSender
        );

        messageList.getChildren().add(row);
        lastSender = displayName;
    }
//    private MeetingChatController chatController;
    public void setChatController(MeetingChatController chatController) {
        this.chatController = chatController;
    }

    private Meeting_participantDTO findParticipantByUserId(String userId) {
        for (Meeting_participantDTO p : participants) {
            if (p.getUserId().equals(userId)) {
                return p;
            }
        }
        return null;
    }



    public void addSystemMessage(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
        label.setAlignment(Pos.CENTER);
        messageList.getChildren().add(label);
    }

    public StackPane getContentPane() {
        return contentPane;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setMeetingController(MeetingController controller) {
        this.meetingController = controller;
    }

    public void showMeetingUI() {
        emptyStatePane.setVisible(false);
        emptyStatePane.setManaged(false);

        rootLayout.setVisible(true);
        rootLayout.setManaged(true);
    }


    public void reset() {
        participantsList.clear();
        tiles.clear();
        videoCallPane.clear();
        currentUser = null;
        roomId = null;
        clearMessages();

        currentUser = null;
        roomId = null;

        rootLayout.setVisible(false);
        rootLayout.setManaged(false);
        emptyStatePane.setVisible(true);
        emptyStatePane.setManaged(true);
    }

}

