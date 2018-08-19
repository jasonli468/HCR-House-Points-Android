package Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class CacheUtil {
    private Context context;
    private final String FILE_NAME = "user_data";

    public void setApplicationContext(Context c) {
        context = c;
    }

    public void writeToCache(String userID, String floorName, String houseName, String name, int permissionLevel) {
        try {
            File file = new File(context.getCacheDir(), FILE_NAME);
            FileWriter fileWriter = new FileWriter(file, false);
            String fileContent = "userID:" + userID +
                    "\nfloorName:" + floorName +
                    "\nhouseName:" + houseName +
                    "\nname:" + name +
                    "\npermissionLevel:" + permissionLevel;
            fileWriter.write(fileContent);
            fileWriter.close();
        } catch (IOException e) {
            Toast.makeText(context, "Error writing cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Error writing cache file", e);
        }
    }

    public boolean cacheFileExists(){
        return new File(context.getCacheDir(), FILE_NAME).exists();
    }

    public void getCacheData(Singleton singleton) {
        try {
            String userID = null;
            String floorName = null;
            String houseName = null;
            String name = null;
            int permissionLevel = 0;
            String line;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(context.getCacheDir(), FILE_NAME)));

            while((line = bufferedReader.readLine()) != null){
                String[] parts =  line.split(":");
                if(parts.length == 2){
                    String key = parts[0];
                    String value = parts[1];
                    switch(key)
                    {
                        case "userID":
                            userID = value;
                            break;
                        case "floorName":
                            floorName = value;
                            break;
                        case "houseName":
                            houseName = value;
                            break;
                        case "name":
                            name = value;
                            break;
                        case "permissionLevel":
                            permissionLevel = Integer.parseInt(value);
                            break;
                    }
                }
            }
            bufferedReader.close();
            singleton.setUserData(floorName, houseName, name, permissionLevel, userID);
        }catch (FileNotFoundException e) {
            Toast.makeText(context, "Could not find cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Could not find cache file", e);
        } catch (IOException e) {
            Toast.makeText(context, "Error reading cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Error reading cache file", e);
        }
    }

    public void deleteCache(){
        if(!new File(context.getCacheDir(), FILE_NAME).delete()){
            Toast.makeText(context, "Could not delete cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Could not delete cache file");
        }
    }
}