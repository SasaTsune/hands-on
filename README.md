# Demo Java CRM

顧客管理のデモアプリケーションです。Spring Boot + Thymeleafを使用したシンプルなCRUD操作を実装しています。

## 機能

- 顧客情報の一覧表示
- 顧客情報の新規登録
- 顧客情報の編集・更新
- 顧客情報の削除

## 技術スタック

- **Java**: 17以上
- **フレームワーク**: Spring Boot 3.2.2
- **テンプレートエンジン**: Thymeleaf
- **ビルドツール**: Maven
- **データストレージ**: インメモリ（List）

## 必要な環境

### Java 17以上のインストール

#### macOSの場合

```bash
# Homebrewを使用する場合
brew install openjdk@17

# パスを通す
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# インストール確認
java -version
```

または、[Oracle公式サイト](https://www.oracle.com/java/technologies/downloads/)または[Adoptium](https://adoptium.net/)からダウンロードしてインストール

#### Windowsの場合

1. [Adoptium](https://adoptium.net/)から最新のJDK 17をダウンロード
2. インストーラーを実行
3. 環境変数`JAVA_HOME`を設定

### Mavenのインストール

#### macOSの場合

```bash
brew install maven

# インストール確認
mvn -version
```

#### Windowsの場合

1. [Apache Maven公式サイト](https://maven.apache.org/download.cgi)からダウンロード
2. 解凍して任意の場所に配置
3. 環境変数`PATH`に`bin`ディレクトリを追加

## セットアップと実行

### 1. プロジェクトディレクトリに移動

```bash
cd /Users/shintaro.itagaki/dev/demo-java-crm
```

### 2. 依存関係のダウンロードとビルド

```bash
mvn clean install
```

### 3. アプリケーションの起動

```bash
mvn spring-boot:run
```

または、JARファイルを生成して実行：

```bash
mvn clean package
java -jar target/demo-java-crm-1.0.0.jar
```

### 4. ブラウザでアクセス

アプリケーションが起動したら、以下のURLにアクセスします：

```
http://localhost:8080/customers
```

## プロジェクト構成

```
demo-java-crm/
├── pom.xml                         # Maven設定ファイル
├── README.md                       # このファイル
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── crm/
        │               ├── CrmApplication.java           # アプリケーション起動クラス
        │               ├── model/
        │               │   └── Customer.java             # 顧客モデル
        │               ├── service/
        │               │   └── CustomerService.java      # ビジネスロジック
        │               └── controller/
        │                   └── CustomerController.java   # Webコントローラー
        └── resources/
            ├── application.properties                    # アプリケーション設定
            ├── static/
            │   └── css/
            │       └── style.css                         # スタイルシート
            └── templates/
                ├── list.html                             # 顧客一覧画面
                └── form.html                             # 登録・編集フォーム
```

## 使い方

### 顧客情報の登録

1. 顧客一覧画面で「新規登録」ボタンをクリック
2. 氏名、メールアドレス（必須）、電話番号、会社名を入力
3. 「保存」ボタンをクリック

### 顧客情報の編集

1. 顧客一覧画面で編集したい顧客の「編集」ボタンをクリック
2. 情報を修正
3. 「保存」ボタンをクリック

### 顧客情報の削除

1. 顧客一覧画面で削除したい顧客の「削除」ボタンをクリック
2. 確認ダイアログで「OK」をクリック

## 開発時のカスタマイズポイント

### ポート番号の変更

[src/main/resources/application.properties](src/main/resources/application.properties) で変更できます：

```properties
server.port=8080  # 任意のポート番号に変更
```

### 顧客モデルの拡張

[src/main/java/com/example/crm/model/Customer.java](src/main/java/com/example/crm/model/Customer.java) にフィールドを追加し、対応するHTMLテンプレートを修正します。

### データの永続化

現在はインメモリ（再起動でデータが消える）ですが、以下の方法で永続化できます：

#### H2データベースを使う場合

1. `pom.xml`に依存関係を追加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. `Customer`エンティティに`@Entity`アノテーションを追加
3. `CustomerRepository`インターフェースを作成（`JpaRepository`を継承）
4. `CustomerService`を修正してリポジトリを使用

## トラブルシューティング

### ポートが既に使用されている

エラー: `Port 8080 was already in use`

**解決策**: 
- `application.properties`でポート番号を変更
- または、既存のプロセスを停止

### Javaのバージョンが古い

エラー: `Unsupported class file major version`

**解決策**:
- Java 17以上をインストール
- `java -version`で確認

### ブラウザで接続できない

**解決策**:
- アプリケーションが正常に起動しているか確認
- コンソールログで `Started CrmApplication` というメッセージを確認
- `http://localhost:8080/customers` に正しくアクセスしているか確認

## ライセンス

このプロジェクトはデモ用途です。自由に使用・改変できます。
