    package main.Client.View;

    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.control.Label;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.layout.VBox;
    import javafx.scene.text.Font;
    import javafx.scene.text.FontWeight;
    import javafx.stage.Stage;
    import main.Client.Controller.MeetingController;
    import main.Client.View.meeting.MeetingUI;
    import shared.MeetingService;

    public class SidebarController {
        private VBox homeItem;
        private VBox meetingItem;
        private StackPane contentPane;
        private VBox sidebar;
        private VBox selectedItem = null;
        private MeetingUI meetingUI;
        private Home homeView;
        private MeetingService meetingService;
        private MeetingController meetingController;

        public SidebarController(MeetingService meetingService) {
            this.meetingService = meetingService;

            sidebar = new VBox(12);
            sidebar.setPrefWidth(80);
            sidebar.setStyle("-fx-background-color: #f2ebfb");

            contentPane = new StackPane();
            contentPane.setStyle("-fx-background-color: #f5f3f4");

            homeView = new Home(contentPane);
            meetingUI = new MeetingUI(contentPane);

            meetingController = new MeetingController(
                    homeView,
                    meetingUI,
                    meetingService,
                    this
            );

            homeView.setMeetingController(meetingController);
            meetingUI.setMeetingController(meetingController);

            // Load danh sach cuoc hop hom nay
            meetingController.loadMeetingsToday();

            // Load danh sach cac cuoc hop gan day
            meetingController.loadRecentMeetings();

            homeItem = createMenuItem("/images/home.png", "Home",
                    () -> {
                    setContent(homeView);
                    homeView.loadMeetings();});

            VBox.setMargin(homeItem, new Insets(30,0,0,0));

            meetingItem = createMenuItem("/images/video.png", "Meet",
                    () -> {
                if (meetingController.isInMeeting()) {
                    meetingUI.showMeetingUI();
                    setContent(meetingUI);
                } else {
                    meetingUI.reset();
                    setContent(meetingUI);
                }
            });


            VBox chattingItem = createMenuItem("/images/chat.png", "Chat",
                    () -> setContent(new ChatPage(contentPane)));

            // MenuItem account setting
            VBox accountItem = createMenuItem("/images/profile.png", "Account",
                    () -> {
    //            String email = loggedInUserEmail; // email bạn lấy từ login
                        String email = "quynhanhnguyen@gmail.com";
                        setContent(new PersonalProfile(contentPane, email));
                    }
            );

            Pane spacer = new Pane();
            VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            VBox logoutItem = createMenuItem("/images/logout.png", "Logout",
                    this::logout);

            sidebar.getChildren().addAll(
                    homeItem, meetingItem, chattingItem, accountItem, spacer, logoutItem
            );

            // Chọn Home mặc định
            selectedItem = null;          // Không có item nào selected
            setContent(homeView);   // Load trang Home
            applySelectedStyle(homeItem);
            selectedItem = homeItem;
        }

        private VBox createMenuItem(String iconPath, String label, Runnable onClick) {

            Image icon = new Image(getClass().getResource(iconPath).toExternalForm());
            Image hoverIcon = new Image(getClass().getResource(iconPath.replace(".png", "_1.png")).toExternalForm());

            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            Label labelItem = new Label(label);
            labelItem.setFont(Font.font("Poppins", FontWeight.NORMAL, 12));
            labelItem.setStyle("-fx-text-fill: #000;");

            VBox vBox = new VBox(5, imageView, labelItem);
            vBox.setPadding(new Insets(12));
            vBox.setAlignment(Pos.CENTER);

            vBox.setUserData(new Image[]{icon, hoverIcon});

            // Hover
            vBox.setOnMouseEntered(e -> {
                if (vBox != selectedItem) applyHoverStyle(vBox);
            });

            vBox.setOnMouseExited(e -> {
                if (vBox != selectedItem) applyNormalStyle(vBox);
            });

            // CLICK —> select item + đổi content
            vBox.setOnMouseClicked(e -> {
                if (selectedItem != null) resetItemStyle(selectedItem);
                selectedItem = vBox;
                applySelectedStyle(vBox);

                // chạy hàm đổi content
                if (onClick != null) onClick.run();
            });

            return vBox;
        }


        // === Style Functions ===

        private void applyNormalStyle(VBox item) {
            Image[] icons = (Image[]) item.getUserData();
            ImageView iconView = (ImageView) item.getChildren().get(0);
            Label label = (Label) item.getChildren().get(1);

            item.setStyle("-fx-background-color: transparent;");
            label.setFont(Font.font("Poppins", FontWeight.NORMAL, 12));
            label.setStyle("-fx-text-fill: #000;");
            iconView.setImage(icons[0]);
        }

        private void applyHoverStyle(VBox item) {
            Image[] icons = (Image[]) item.getUserData();
            ImageView iconView = (ImageView) item.getChildren().get(0);
            Label label = (Label) item.getChildren().get(1);

            item.setStyle("-fx-background-color: #fff; -fx-background-radius: 10;");
            label.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
            label.setStyle("-fx-text-fill: #872AFF;");
            iconView.setImage(icons[1]);
        }

        private void applySelectedStyle(VBox item) {
            Image[] icons = (Image[]) item.getUserData();
            ImageView iconView = (ImageView) item.getChildren().get(0);
            Label label = (Label) item.getChildren().get(1);

            item.setStyle("-fx-background-color: #fff; -fx-background-radius: 10;");
            label.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
            label.setStyle("-fx-text-fill: #872AFF;");
            iconView.setImage(icons[1]);
        }

        private void resetItemStyle(VBox item) {
            applyNormalStyle(item);
        }

        // === Content & Logout ===

        private void setContent(javafx.scene.Node newContent) {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newContent);
        }

        private void logout() {
            try {
                Stage currentStage = (Stage) sidebar.getScene().getWindow();
                currentStage.close();

                LogIn logIn = new LogIn();
                Stage newStage = new Stage();
                logIn.start(newStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public VBox getSidebar() { return sidebar; }
        public StackPane getContentPane() { return contentPane; }

        // Cap nhat item Meeting duoc chon khi join cuoc hop thanh cong
        public void selectMeetingItem() {
            if (selectedItem != null) {
                resetItemStyle(selectedItem);
            }
            applySelectedStyle(meetingItem);
            selectedItem = meetingItem;
        }

        public void selectHomeItem() {
            if (selectedItem != null) {
                resetItemStyle(selectedItem); // reset style item cũ
            }
            applySelectedStyle(homeItem);     // highlight Home
            selectedItem = homeItem;          // cập nhật selectedItem
            setContent(homeView);             // load giao diện Home
        }
    }
