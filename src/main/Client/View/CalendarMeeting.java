package main.Client.View;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CalendarMeeting extends StackPane {
    private StackPane contentPane;

    public CalendarMeeting(StackPane contentPane) {
        this.contentPane = contentPane;

        Label label = new Label("Calendar Meeting page");
        getChildren().add(label);

        Button backBtn = new Button("Back to Home");
        backBtn.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(new Home(contentPane));
        });
        getChildren().add(backBtn);
    }
}

