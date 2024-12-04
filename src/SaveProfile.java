import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveProfile {
    public static void saveProfile(ProfileSave save) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("profiles/" + save.getUsername() +".sav"))) {
            oos.writeObject(save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
