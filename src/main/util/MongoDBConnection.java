package main.util;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoDatabase database;
    public static MongoDatabase getDatabase() {
        // Tạo kết nối MongoDB
        if (database == null) {
            try {
                MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
                database = mongoClient.getDatabase("halomeet");
                // Kiểm tra connection
                System.out.println("Connected successfully to : " + database.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return database;
    }
}
