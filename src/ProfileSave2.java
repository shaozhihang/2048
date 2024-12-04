public class ProfileSave2 implements Comparable<ProfileSave2> {
    private String username;
    private int score;

    public ProfileSave2(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(ProfileSave2 other) {
        return Integer.compare(other.score,this.score);
    }

    @Override
    public String toString() {
        return username+"   "+score;
    }
}