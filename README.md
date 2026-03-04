# Demo Java CRM

顧客管理のデモアプリケーションです。Spring Boot + Thymeleafを使用したシンプルなCRUD操作を実装しています。

## 機能

- 顧客情報の一覧表示
- 顧客情報の新規登録
- 顧客情報の編集・更新
- 顧客情報の削除
- アカウントプランの管理（顧客ごとのプラン表示・登録・編集・削除）
- マークダウン形式でアカウントプランを入力・リアルタイムプレビュー・ HTMLレンダリング表示
- 顧客一覧画面からアカウントプランへの遷移

## 技術スタック

- **Java**: 17以上
- **フレームワーク**: Spring Boot 3.2.2
- **テンプレートエンジン**: Thymeleaf
- **ビルドツール**: Maven
- **マークダウン変換**: commonmark-java 0.24.0（サーバーサイド） / marked.js（クライアントサイドプレビュー）
- **データベース**: PostgreSQL 16
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
cd demo-java-crm-rdm-migration
```

### 2. 環境変数の設定

`.env.example` をコピーして `.env` ファイルを作成し、データベースの認証情報を設定します：

```bash
cp .env.example .env
```

`.env` ファイルを編集して、適切な値を設定してください：

```
POSTGRES_DB=your_database_name
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password
```

### 3. コンテナの起動

```bash
docker compose up -d
```

### 4. ブラウザでアクセス

コンテナが起動したら、以下のURLにアクセスします：

```
http://localhost:8080/customers
```

## プロジェクト構成

```
demo-java-crm/
├── docker-compose.yml                  # Docker Compose設定
├── .env.example                        # 環境変数テンプレート
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
        │               │   ├── Customer.java             # 顧客エンティティ
        │               │   └── AccountPlan.java          # アカウントプランエンティティ
        │               ├── repository/
        │               │   └── CustomerRepository.java   # データアクセス（JPA）
        │               ├── service/
        │               │   ├── CustomerService.java      # 顧客ビジネスロジック
        │               │   ├── AccountPlanService.java   # アカウントプランビジネスロジック
        │               │   └── MarkdownService.java      # マークダウン変換サービス
        │               └── controller/
        │                   ├── CustomerController.java      # 顧客コントローラー
        │                   ├── AccountPlanController.java   # アカウントプランコントローラー
        │                   └── GlobalExceptionHandler.java  # 例外ハンドリング
        └── resources/
                ├── application.properties                    # アプリケーション設定
                ├── schema.sql                                # テーブル定義（PostgreSQL DDL）
                ├── data.sql                                  # 初期データ投入（PostgreSQL構文）
            ├── static/
            │   └── css/
            │       └── style.css                         # スタイルシート
            └── templates/
                ├── list.html                             # 顧客一覧画面
                ├── form.html                             # 顧客登録・編集フォーム
                ├── error.html                            # エラー画面
                └── account-plan/
                    ├── show.html                         # アカウントプラン表示画面（マークダウンHTMLレンダリング）
                    └── form.html                         # アカウントプラン登録・編集フォーム（マークダウンプレビュー付き）
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

### アカウントプランの管理

1. 顧客一覧画面で「プラン」ボタンをクリックしてアカウントプラン画面に遷移
2. 「新規作成」ボタンでマークダウン形式のプラン内容を登録
3. 「プレビュー表示」ボタンでマークダウンのリアルタイムプレビューを確認可能
4. 保存すると表示画面でマークダウンがHTMLとしてレンダリング表示される
5. 登録済みのプランは「編集」ボタンで内容を更新、「削除」ボタンで削除可能

## 開発時のカスタマイズポイント

### ポート番号の変更

[src/main/resources/application.properties](src/main/resources/application.properties) で変更できます：

```properties
server.port=8080  # 任意のポート番号に変更
```

### 顧客モデルの拡張

[src/main/java/com/example/crm/model/Customer.java](src/main/java/com/example/crm/model/Customer.java) にフィールドを追加し、対応するHTMLテンプレートを修正します。

### データベース構成

本アプリケーションは PostgreSQL 16 を使用してデータを永続化しています。

- **テーブル定義**: `src/main/resources/schema.sql` で `customers` テーブルとシーケンスを作成（PostgreSQL DDL）
- **初期データ**: `src/main/resources/data.sql` でサンプルデータを投入（`nextval('customers_seq')` によるID自動採番）
- **ORM**: Spring Data JPA（`CustomerRepository` が `JpaRepository` を継承）
- **接続設定**: `application.properties` で環境変数プレースホルダーを使用した PostgreSQL データソースを設定
- **認証情報**: `.env` ファイルで管理（`.env.example` をテンプレートとして使用）

Docker Compose でアプリケーションと PostgreSQL が同時に起動し、DB の準備完了（ヘルスチェック）後にアプリケーションが接続します。

> **注意**: アカウントプラン機能はインメモリ（ArrayList）で管理されており、アプリケーション再起動時にデータはリセットされます。

## ライセンス

このプロジェクトはデモ用途です。自由に使用・改変できます。
