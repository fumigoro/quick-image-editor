## 2020年度プログラミング実践1 最終課題の作品

ソースコード本体は```/app/src/main/java```の中に、外部の設定ファイル```settings.json```と```config.properties```は```/app/src/main/resources```にあります。

## 実行方法
本プロジェクトはビルド自動化システム「gradle」を使用しています。

### Gradleのインストール
[公式サイト](https://gradle.org/install/)を参照。

### リポジトリをクローン

```
git clone https://github.com/fumigoro/quick-image-editor.git
```

### クローンしたディレクトリへ移動し実行
gradle runコマンドで依存ライブラリの取得やコンパイル、実行が自動で行われます。
```
cd quick-image-editor
gradle run
```
