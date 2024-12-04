import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadGame {
    public static GameSave loadGame(String username,String version,String mode) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("saves/" + username +"_"+mode+"_"+version+ ".sav"))) {
            return (GameSave) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game save for user: "+username);
            e.printStackTrace();
            return null;
        }
    }
    public static boolean isSaveFileValid(String username,String version,String mode) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("saves/" + username +"_"+mode+"_"+version+ ".sav"))) {
            GameSave save = (GameSave) ois.readObject();
            // 如果读取成功且没有抛出异常，文件是有效的
            return true;
        } catch (IOException | ClassNotFoundException e) {
            // 如果捕获到异常，文件可能已损坏或不存在
            return false;
        }
    }
}
