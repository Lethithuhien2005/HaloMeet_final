package main.Client.Controller;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import main.Client.View.Home;
import main.Client.View.SidebarController;
import main.Client.View.meeting.MeetingUI;
import main.util.DialogUtil;
import main.util.Session;
import shared.DTO.RoomDTO;
import shared.MeetingClientCallback;
import shared.MeetingService;

import java.rmi.RemoteException;
import java.util.List;

public class MeetingController {
    private Home homeView;
    private MeetingUI meetingUI;
    private MeetingService meetingService;
    private MeetingClientCallback callback;
    private SidebarController sidebarController;
    private boolean isInMeeting = false;

    public MeetingController(Home homeView, MeetingUI meetingUI, MeetingService meetingService, SidebarController sidebarController) {
        this.homeView = homeView;
        this.meetingUI = meetingUI;
        this.meetingService = meetingService;
        this.sidebarController = sidebarController;

        try {
            this.callback = new MeetingClientCallbackImplement(homeView, meetingUI, sidebarController, Session.getInstance().getUserIdHex(), this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Khi nguoi dung click vao button Create a new meeting
    public void onClickCreatMeeting() {
        String titleMeeting = homeView.titleTextField.getText();
        String passcode = homeView.passwordMeeting.getText();
        String userID = Session.getInstance().getUserIdHex();

        if (titleMeeting.isEmpty()) {
            Platform.runLater(() -> {
                DialogUtil.showError("Error", null, "Please fill in the title meeting.");
            });
            return;
        }
        // Kiem tra userID tranh TH Server fail
        if (userID == null ) {
            Platform.runLater(() -> {
                DialogUtil.showError("Error", null, "User not logged in.");
            });
            return;
        }

        new Thread(() -> {
            try {
                meetingService.createMeeting(userID, titleMeeting, passcode, callback);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> DialogUtil.showError("Connect to server", null, "TCP not connected!"));
            }
        }).start();
    }

    // Khi nguoi dung click vao button Join the meeting now hoac Join now
    public void onClickJoinTheMeeting(String meetingCode, String passcode) {
        String userID = Session.getInstance().getUserIdHex();

        if (meetingCode.isEmpty()) {
            Platform.runLater(() -> {
                DialogUtil.showError("Error", null, "Please fill in the meeting ID");
            });
            return;
        }

        // Kiem tra userID tranh TH Server fail
        if (userID == null ) {
            Platform.runLater(() -> {
                DialogUtil.showError("Error", null, "User not logged in.");
            });
            return;
        }

        new Thread(() -> {
            try {
                meetingService.joinMeeting(userID, meetingCode, passcode, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> DialogUtil.showError("RMI Error", null, "Cannot connect to server!"));
                }
        }).start();

    }

    public void loadMeetingsToday() {
        String userId = Session.getInstance().getUserIdHex();

        if (userId == null ) {
            Platform.runLater(() -> {
                DialogUtil.showError("Error", null, "User not logged in.");
            });
            return;
        }

        new Thread(() -> {
            try {
                List<RoomDTO> meetings = meetingService.getMeetingsToday(userId);

                Platform.runLater(() -> {
                    homeView.clearMeetingsToday();

                    if (meetings.isEmpty()) {
                        homeView.showNoMeeting();
                    } else {
                        meetings.forEach(m ->
                                homeView.addMeetingToday(
                                        m.getTitle(),
                                        m.getMeeting_code(),
                                        m.getPasscode(),
                                        m.getCreated_at()
                                )
                        );
                    }
                });

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void loadRecentMeetings() {
        new Thread(() -> {
            try {
                String userId = Session.getInstance().getUserIdHex();
                List<RoomDTO> meetings =
                        meetingService.getRecentMeetings(userId);

                Platform.runLater(() -> {
                    homeView.showRecentMeetings(meetings);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void leaveMeeting() {
        String roomId = meetingUI.getRoomId();
        String userId = Session.getInstance().getUserIdHex();

        // Kiem tra userID tranh TH Server fail
        if (userId == null ) {
            Platform.runLater(() -> {
                DialogUtil.showError("Error", null, "User not logged in.");
            });
            return;
        }
        if (roomId == null) {
            Platform.runLater(() -> {
                DialogUtil.showError("Leave meeting", null, "roomID is null");
            });
        }

        new Thread(() -> {
            try {
                meetingService.leaveMeeting(userId, roomId, callback);
                // Hien thi trang home
                Platform.runLater(()-> {
                    setInMeeting(false);
                    meetingUI.reset();
                    sidebarController.selectHomeItem();
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setMicUser(String targetId) {
        String roomId = meetingUI.getRoomId();
        String currentUser = Session.getInstance().getUserIdHex();

        if ( currentUser == null)  {
            Platform.runLater(() -> {
                DialogUtil.showError("Set mic error", null, "User not logged in");
            });
            return;
        };
        if (roomId == null) {
            Platform.runLater(() -> {
                DialogUtil.showError("Set mic user", null, "Cannot set mic other because roomID is null");
            });
        }

        new Thread(() -> {
            try {
                meetingService.setMic(roomId, currentUser, targetId);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> DialogUtil.showError("RMI Error", null, "Cannot connect to server!"));
            }
        }).start();

    }

    public void setCameraUser(String targetId) {
        String roomId = meetingUI.getRoomId();
        String currentUser = Session.getInstance().getUserIdHex();
        if ( currentUser == null)  {
            Platform.runLater(() -> {
                DialogUtil.showError("Set mic error", null, "User not logged in");
            });
            return;
        };
        if (roomId == null) {
            Platform.runLater(() -> {
                DialogUtil.showError("Set mic user", null, "Cannot set mic other because roomID is null");
            });
        }

        new Thread(() -> {
            try {
                meetingService.setCam(roomId, currentUser, targetId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void kickUser(String targetId) {
        String roomId = meetingUI.getRoomId();
        String currentUser = Session.getInstance().getUserIdHex();
        if ( currentUser == null)  {
            Platform.runLater(() -> {
                DialogUtil.showError("Set mic error", null, "User not logged in");
            });
            return;
        };
        if (roomId == null) {
            Platform.runLater(() -> {
                DialogUtil.showError("Set mic user", null, "Cannot set mic other because roomID is null");
            });
        }

        new Thread(() -> {
            try {
                meetingService.kickUser(roomId, currentUser, targetId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isInMeeting() {
        return isInMeeting;
    }

    public void setInMeeting(boolean inMeeting) {
        this.isInMeeting = inMeeting;
    }

}
