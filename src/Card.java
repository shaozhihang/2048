import java.awt.*;

public class Card {
    private int x = 0;
    private int y = 0;
    private int w = 80;
    private int h = 80;
    private int i = 0;
    private int j = 0;
    private int start = 10;
    private int num = 0;
    private boolean merge = false;

    private String mode;

    public Card(int i, int j,String mode) {
        this.i = i;
        this.j = j;
        this.mode=mode;
        this.cal();
        if(mode.equals("5plus5")) {
            w=60;
            h=60;
        }
        else if(mode.equals("classic")) {
            w=80;
            h=80;
        }
    }

    public void cal() {
        this.x = this.start + this.j * this.w + (this.j + 1) * (mode.equals("5plus5")?-9:5);
        this.y = this.start + this.i * this.h + (this.i + 1) * (mode.equals("5plus5")?-9:5);
    }

    public void draw(Graphics g) {
        Color color = this.getColor();
        Color oColor = g.getColor();
        g.setColor(color);
        g.fillRoundRect(this.x, this.y, this.w, this.h, 4, 4);
        if (this.num != 0) {
            g.setColor(new Color(10, 10, 100));
            Font font = new Font("思源宋体", 1, 28);
            g.setFont(font);
            String text = "" + this.num;
            int textLen = getWordWidth(font, text, g);
            int tx = this.x + (this.w - textLen) / 2;
            int ty = this.y + 50;
            g.drawString(text, tx, ty);
        }

        g.setColor(oColor);
    }

    public static int getWordWidth(Font font, String content, Graphics g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int width = 0;

        for(int i = 0; i < content.length(); ++i) {
            width += metrics.charWidth(content.charAt(i));
        }

        return width;
    }

    private Color getColor() {
        Color color = null;
        switch (this.num) {
            case 2:
                color = new Color(127, 255, 212);
                break;
            case 4:
                color = new Color(52, 251, 152);
                break;
            case 8:
                color = new Color(0, 160, 164);
                break;
            case 16:
                color = new Color(64, 224, 208);
                break;
            case 32:
                color = new Color(0, 191, 255);
                break;
            case 64:
                color = new Color(0, 128, 128);
                break;
            case 128:
                color = new Color(70, 130, 180);
                break;
            case 256:
                color = new Color(173, 255, 47);
                break;
            case 512:
                color = new Color(0, 255, 0);
                break;
            case 1024:
                color = new Color(17, 139, 34);
                break;
            case 2048:
                color = new Color(0, 100, 0);
                break;
            default:
                color = new Color(170, 196, 237);
        }

        return color;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return this.num;
    }

    public boolean moveTop(Card[][] cards, boolean b,GamePanel panel) {
        if (this.i == 0) {
            return false;
        } else {
            Card prev = cards[this.i - 1][this.j];
            if (prev.getNum() == 0) {
                if (b) {
                    prev.num = this.num;
                    this.num = 0;
                    prev.moveTop(cards, b,panel);
                }

                return true;
            } else if (prev.getNum() == this.num && !prev.merge) {
                if (b) {
                    prev.merge = true;
                    prev.num = this.num * 2;
                    panel.setScore(panel.getScore()+prev.num);
                    this.num = 0;
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public boolean moveRight(Card[][] cards, boolean b,GamePanel panel) {
        if (this.j == (mode.equals("5plus5")?4:3)) {
            return false;
        } else {
            Card prev = cards[this.i][this.j + 1];
            if (prev.getNum() == 0) {
                if (b) {
                    prev.num = this.num;
                    this.num = 0;
                    prev.moveRight(cards, b,panel);
                }

                return true;
            } else if (prev.getNum() == this.num && !prev.merge) {
                if (b) {
                    prev.merge = true;
                    prev.num = this.num * 2;
                    panel.setScore(panel.getScore()+prev.num);
                    this.num = 0;
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public boolean moveDown(Card[][] cards, boolean b,GamePanel panel) {
        if (this.i == (mode.equals("5plus5")?4:3)) {
            return false;
        } else {
            Card prev = cards[this.i + 1][this.j];
            if (prev.getNum() == 0) {
                if (b) {
                    prev.num = this.num;
                    this.num = 0;
                    prev.moveDown(cards, b,panel);
                }

                return true;
            } else if (prev.getNum() == this.num && !prev.merge) {
                if (b) {
                    prev.merge = true;
                    prev.num = this.num * 2;
                    panel.setScore(panel.getScore()+prev.num);
                    this.num = 0;
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public boolean moveLeft(Card[][] cards, boolean b,GamePanel panel) {
        if (this.j == 0) {
            return false;
        } else {
            Card prev = cards[this.i][this.j - 1];
            if (prev.getNum() == 0) {
                if (b) {
                    prev.num = this.num;
                    this.num = 0;
                    prev.moveLeft(cards, b,panel);
                }

                return true;
            } else if (prev.getNum() == this.num && !prev.merge) {
                if (b) {
                    prev.merge = true;
                    prev.num = this.num * 2;
                    panel.setScore(panel.getScore()+prev.num);
                    this.num = 0;
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public void setMerge(boolean b) {
        this.merge = b;
    }
}

