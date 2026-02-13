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
- **データベース**: Oracle Database Free 23c
- **ORM**: Spring Data JPA
- **インフラ**: Docker / Docker Compose

## 必要な環境

### Docker / Docker Compose のインストール

本アプリケーションの実行には Docker および Docker Compose が必要です。

- **Mac**: [Docker Desktop for Mac](https://docs.docker.com/desktop/install/mac-install/) をインストール（Docker Compose 同梱）
- **Windows**: [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/) をインストール（Docker Compose 同梱）
- **Linux**: [Docker Engine](https://docs.docker.com/engine/install/) と [Docker Compose](https://docs.docker.com/compose/install/) をそれぞれインストール

インストール後、以下のコマンドで正常にインストールされていることを確認してください：

```bash
docker --version
docker compose version
```

## セットアップと実行

### 1. プロジェクトディレクトリに移動

```bash
cd demo-java-crm
```

### 2. コンテナの起動

```bash
docker-compose up -d
```

### 3. ブラウザでアクセス

コンテナが起動したら、以下のURLにアクセスします：

```
http://localhost:8080/customers
```

## プロジェクト構成

```
demo-java-crm/
├── docker-compose.yml                  # Docker Compose設定
├── docker/
│   └── app/
│       └── Dockerfile                  # Spring Bootアプリ用Dockerfile
├── pom.xml                             # Maven設定ファイル
├── README.md                           # このファイル
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── crm/
        │               ├── CrmApplication.java           # アプリケーション起動クラス
        │               ├── model/
        │               │   └── Customer.java             # 顧客エンティティ
        │               ├── repository/
        │               │   └── CustomerRepository.java   # データアクセス（JPA）
        │               ├── service/
        │               │   └── CustomerService.java      # ビジネスロジック
        │               └── controller/
        │                   └── CustomerController.java   # Webコントローラー
        └── resources/
            ├── application.properties                    # アプリケーション設定
            ├── schema.sql                                # テーブル定義（DDL）
            ├── data.sql                                  # 初期データ投入
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
2. 氏名（必須）、メールアドレス（必須）、電話番号、会社名を入力
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

### データベース構成

本アプリケーションは Oracle Database Free 23c を使用してデータを永続化しています。

- **テーブル定義**: `src/main/resources/schema.sql` で `CUSTOMERS` テーブルとシーケンスを作成
- **初期データ**: `src/main/resources/data.sql` でサンプルデータを投入
- **ORM**: Spring Data JPA（`CustomerRepository` が `JpaRepository` を継承）
- **接続設定**: `application.properties` で Oracle データソースを設定

Docker Compose でアプリケーションと Oracle Database が同時に起動し、DB の準備完了後にアプリケーションが接続します。

## ライセンス

このプロジェクトはデモ用途です。自由に使用・改変できます。
