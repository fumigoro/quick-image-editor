// package quick.image.editor;

public class Setting{
    public static String path;
    public static String fileName;
    public static String format = "path";
    public static Size size;
    public Setting(){
        this.path = "";
        this.fileName = "";
        this.format = "";
        this.size = new Size();
    }
}

class Size{
    public static SizePreset[] presets;
    public static boolean resize;
    public static boolean fixAspectRatio;
    public static int width;
    public static int hight;
    public Size(){
        this.presets = new SizePreset[2];
        this.presets[0] = new SizePreset();
        this.presets[1] = new SizePreset();
        
        this.resize = false;
        this.fixAspectRatio = false;
        this.width = 0;
        this.hight = 0;
    }
}

class SizePreset{
    public static int width;
    public static int hight;
    public SizePreset(){
        this.width = 0;
        this.hight = 0;
    }
}
