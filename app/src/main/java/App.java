/*
 * メインのクラス
 * メイン画面をインスタンス化する。
 * MEMO:将来的に複数ウィンドウの同時立ち上げ等を実装するときのためにこのような構成にしている
 */
package quick.image.editor;

public class App {

    App() {

    }

    public static void main(String[] args) {
        MainWindow mw = new MainWindow();
    }

}
