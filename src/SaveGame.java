import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveGame {//序列化对象存储
    public static void saveGame(GameSave save,String version) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("saves/" + save.getUsername() + "_"+save.getMode()+"_"+ version +".sav"))) {
            oos.writeObject(save);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
