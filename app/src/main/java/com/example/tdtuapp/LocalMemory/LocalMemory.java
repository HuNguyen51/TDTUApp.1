package com.example.tdtuapp.LocalMemory;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public final class LocalMemory {
    // lưu người dùng trên máy vào bộ nhớ cục bộ
    public static void saveLocalUser(Context context, String data){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("user.txt", context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String loadLocalUser(Context context){
        String data = "";
        try {
            FileInputStream fileInputStream = context.openFileInput("user.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Lưu thời gian của tin nhắn cuối cùng
    public static void saveLastMessageTime(Context context, String chatID){
        String time = String.valueOf(System.currentTimeMillis());
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(chatID+".txt", context.MODE_PRIVATE);
            fileOutputStream.write(time.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String loadLastMessageTime(Context context, String chatID){
        String data = "0";
        try {
            FileInputStream fileInputStream = context.openFileInput(chatID+".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static int getNotifyID(Context context){
        int id = 0;
        // đọc file
        try {
            FileInputStream fileInputStream = context.openFileInput("notification_id"+".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            id = Integer.parseInt(stringBuilder.toString()) + 1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ghi file
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("notification_id"+".txt", context.MODE_PRIVATE);
            fileOutputStream.write(String.valueOf(id).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }
}
