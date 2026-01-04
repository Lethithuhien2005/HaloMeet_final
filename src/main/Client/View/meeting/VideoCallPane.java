package main.Client.View.meeting;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;


import java.util.List;

public class VideoCallPane extends GridPane {
    public VideoCallPane() {
        this.setHgap(10);
        this.setVgap(10);
        this.setStyle("-fx-background-color: #fff;");
    }

    public void updateLayout(List<VideoTile> tiles) {
        if (tiles.isEmpty()) return;

        this.getChildren().clear();
        int n = tiles.size();

        // Clear all constraints
        this.getColumnConstraints().clear();
        this.getRowConstraints().clear();

        // === CASE 1: 1 USER ===
        if (n == 1) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100);
            this.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100);
            this.getRowConstraints().add(row);

            add(tiles.get(0), 0, 0);
            return;
        }

        // === CASE 2: 2 users (1 row) ===
        if (n == 2) {
            this.getColumnConstraints().clear();
            this.getRowConstraints().clear();
            this.setAlignment(Pos.CENTER);

            for (int i = 0; i < 2; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(50);     // CHIA ĐỀU 2 CỘT
                this.getColumnConstraints().add(col);
            }
            // set height row
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(50);
            this.getRowConstraints().add(row);

            add(tiles.get(0), 0, 0);
            add(tiles.get(1), 1, 0);
            return;
        }
        // === CASE 3: 3 users (1 row)
        if (n == 3) {
            this.setAlignment(Pos.CENTER);
            this.getRowConstraints().clear();
            this.getColumnConstraints().clear();

            for (int i = 0; i < 3; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(100.0 / 3);
                this.getColumnConstraints().add(col);
            }

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(45);
            this.getRowConstraints().add(row);

            add(tiles.get(0), 0, 0);
            add(tiles.get(1), 1, 0);
            add(tiles.get(2), 2, 0);
            return;
        }

        // === CASE 4: exactly 4 users (2x2 grid) ===
        if (n == 4) {
            // 2 columns 50/50
            for (int i = 0; i < 2; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(50);
                this.getColumnConstraints().add(col);
            }

            // 2 rows 50/50
            for (int i = 0; i < 2; i++) {
                RowConstraints row = new RowConstraints();
                row.setPercentHeight(50);
                this.getRowConstraints().add(row);
            }

            int index = 0;
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    add(tiles.get(index++), col, row);
                }
            }

            return;
        }

        // === CASE 4: ≥5 users ===
        // Force equal 3 columns
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 3);
            this.getColumnConstraints().add(col);
        }

        // Set 2 hàng: trên 70%, dưới 30%
        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        r1.setPercentHeight(70);
        r2.setPercentHeight(30);
        this.getRowConstraints().addAll(r1, r2);

        // Main video span 3 columns
        add(tiles.get(0), 0, 0, 3, 1);

        int small = Math.min(n - 1, 3);
        for (int i = 0; i < small; i++) {
            add(tiles.get(i + 1), i, 1);
        }

        // ===== BUỘC TẤT CẢ TILE GIÃN ĐỀU =====
        for (VideoTile tile : tiles) {
            GridPane.setHgrow(tile, Priority.ALWAYS);
            GridPane.setVgrow(tile, Priority.ALWAYS);
            tile.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

    }

    public void clear() {
        // Xóa toàn bộ video tiles
        this.getChildren().clear();

        // Xóa layout constraints cũ
        this.getColumnConstraints().clear();
        this.getRowConstraints().clear();

        // Reset alignment (quan trọng)
        this.setAlignment(Pos.CENTER);
    }


}
