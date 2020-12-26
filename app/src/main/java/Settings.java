package quick.image.editor;

public class Settings{
    public static Setting defaults;
    public static Setting recent;
    Settings(Setting defaults,Setting recent){
        this.defaults = defaults;
        this.recent = recent;
    }
}

class Setting{
    public static String path;
    public static String fileName;
    public static String format;
    public static Size size;
    public Setting(String path,String fileName,String format,Size size){
        this.path = path;
        this.fileName = fileName;
        this.format = format;
        this.size = size;
    }
}

class Size{
    public static SizePreset[] presets;
    public static boolean resize;
    public static boolean fixAspectRatio;
    public static int width;
    public static int hight;
    public Size(SizePreset[] presets,boolean resize,boolean fixAspectRatio,int width,int hight){
        this.presets = presets;
        this.resize = resize;
        this.fixAspectRatio = fixAspectRatio;
        this.width = width;
        this.hight = hight;
    }
}

class SizePreset{
    public static int width;
    public static int hight;
    public SizePreset(int width,int hight){
        this.width = width;
        this.hight = hight;
    }
}
