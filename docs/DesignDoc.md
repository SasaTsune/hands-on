# Demo Java CRM - Design Document

## Context/Background（背景）

本ドキュメントは、Demo Java CRMアプリケーションの設計と実装を記録するものです。このアプリケーションは、Spring Boot + Thymeleafを使用した顧客管理システムのデモンストレーションとして開発されました。

開発の目的は、Spring Bootを用いたWebアプリケーション開発の基本的なパターン（MVC、CRUD操作、テンプレートエンジン）を学習・理解するためのサンプルコードを提供することです。本番環境での使用を想定したものではなく、教育・学習目的のシンプルな実装となっています。

顧客データの永続化には PostgreSQL 16 を採用しており、Docker Compose でアプリケーションとデータベースが同時に起動します。アカウントプランデータはインメモリ（ArrayList）で管理されており、アプリケーション再起動時にリセットされます。

## 機能一覧

本アプリケーションは以下の機能を提供します。

### アカウントプラン管理機能

顧客に紐づくアカウントプラン情報を管理します。アカウントプランの内容はマークダウン形式で記述でき、入力フォームではリアルタイムプレビューが可能です。保存済みのプランは表示画面でHTMLとしてレンダリングされます。`AccountPlanService`により、顧客IDに紐づくプランの取得、新規登録、更新、削除の操作が可能です。データはインメモリ（ArrayList）で管理されます。`AccountPlanController`が画面遷移とCRUD操作のエンドポイントを提供します。

エンドポイント:
- `GET /customers/{customerId}/account-plan`（プラン表示）
- `GET /customers/{customerId}/account-plan/new`（登録フォーム表示）
- `POST /customers/{customerId}/account-plan/save`（保存処理）
- `GET /customers/{customerId}/account-plan/edit`（編集フォーム表示）
- `POST /customers/{customerId}/account-plan/delete`（削除処理）

存在しない顧客IDへのアクセス時は`IllegalArgumentException`をスローし、エラーハンドリングします。

### 顧客一覧表示機能

顧客情報をテーブル形式で一覧表示します。各顧客レコードに対して編集・削除操作へのリンクを提供します。顧客が登録されていない場合は、空の状態を示すメッセージを表示します。

エンドポイント: `GET /customers`

### 顧客新規登録機能

新規顧客情報を登録するためのフォームを提供します。氏名とメールアドレスは必須入力項目です。電話番号と会社名は任意入力項目です。登録完了後は顧客一覧画面にリダイレクトします。

エンドポイント: `GET /customers/new`（フォーム表示）、`POST /customers/save`（登録処理）

### 顧客情報編集機能

既存の顧客情報を編集するためのフォームを提供します。新規登録フォームと同じテンプレートを使用し、`isEdit`フラグで表示を切り替えます。更新完了後は顧客一覧画面にリダイレクトします。

エンドポイント: `GET /customers/edit/{id}`（フォーム表示）、`POST /customers/save`（更新処理）

### 顧客削除機能

指定されたIDの顧客情報を削除します。削除前にJavaScriptによる確認ダイアログを表示します。削除完了後は顧客一覧画面にリダイレクトします。

エンドポイント: `POST /customers/delete/{id}`

## Design（設計）

### アーキテクチャ

本アプリケーションはレイヤードMVCアーキテクチャを採用しています。

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  (Thymeleaf Templates: list.html, form.html,                │
│   account-plan/show.html, account-plan/form.html)           │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                     Controller Layer                         │
│        (CustomerController, AccountPlanController)            │
│  - HTTPリクエストの受付とルーティング                          │
│  - Modelへのデータ設定                                        │
│  - ビュー名の返却                                             │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│             (CustomerService, AccountPlanService)             │
│  - ビジネスロジックの実装                                      │
│  - データアクセスの抽象化                                      │
│  - ID生成（PostgreSQL Sequence / AtomicLong）               │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  - PostgreSQL 16 (Customer: JPA + customersテーブル)    │
│  - インメモリ (AccountPlan: ArrayList)                    │
└─────────────────────────────────────────────────────────────┘
```

### データモデル

#### 顧客情報（Customer）

顧客情報は`Customer`クラスで表現されます。

| フィールド | 型 | 必須 | 説明 |
|-----------|------|------|------|
| id | Long | 自動生成 | 一意識別子。PostgreSQLシーケンス（`customers_seq`）による連番 |
| name | String | 必須 | 顧客氏名 |
| email | String | 必須 | メールアドレス |
| phone | String | 任意 | 電話番号 |
| company | String | 任意 | 会社名 |

#### アカウントプラン（AccountPlan）

アカウントプランは`AccountPlan`クラスで表現されます。顧客に紐づくプラン情報をマークダウン形式で管理します。

| フィールド | 型 | 必須 | 説明 |
|-----------|------|------|------|
| id | Long | 自動生成 | 一意識別子。AtomicLongによる連番 |
| customerId | Long | 必須 | 紐づく顧客のID |
| content | String | 任意 | アカウントプランの内容（マークダウン形式） |
| createdAt | LocalDateTime | 自動設定 | 作成日時 |
| updatedAt | LocalDateTime | 自動設定 | 更新日時 |

### 技術スタック

| カテゴリ | 技術 | バージョン |
|---------|------|-----------|
| 言語 | Java | 17 |
| フレームワーク | Spring Boot | 3.2.2 |
| テンプレートエンジン | Thymeleaf | Spring Boot管理 |
| データベース | PostgreSQL | 16 |
| JDBCドライバ | postgresql | Spring Boot管理 |
| ORM | Spring Data JPA (Hibernate) | Spring Boot管理 |
| ビルドツール | Maven | - |
| マークダウン変換 | commonmark-java / marked.js | - |
| Webサーバー | 組み込みTomcat | Spring Boot管理 |
| インフラ | Docker / Docker Compose | - |

### ディレクトリ構成

```
demo-java-crm/
├── docker-compose.yml                         # Docker Compose設定
├── .env.example                               # 環境変数テンプレート
├── docker/
│   └── app/
│       └── Dockerfile                         # Spring Bootアプリ用Dockerfile
├── pom.xml                                    # Maven設定
├── README.md                                  # プロジェクト説明
├── docs/
│   └── DesignDoc.md                           # 本ドキュメント
└── src/main/
    ├── java/com/example/crm/
    │   ├── CrmApplication.java                # アプリケーション起動クラス
    │   ├── controller/
    │   │   ├── CustomerController.java        # 顧客コントローラー
    │   │   ├── AccountPlanController.java     # アカウントプランコントローラー
    │   │   └── GlobalExceptionHandler.java    # 例外ハンドリング
    │   ├── model/
    │   │   ├── Customer.java                  # 顧客ドメインモデル
    │   │   └── AccountPlan.java               # アカウントプランドメインモデル
    │   └── service/
    │       ├── CustomerService.java           # 顧客ビジネスロジック
    │       ├── AccountPlanService.java        # アカウントプランビジネスロジック
    │       └── MarkdownService.java           # マークダウン変換サービス
    └── resources/
        ├── application.properties             # アプリケーション設定
        ├── schema.sql                         # テーブル定義（PostgreSQL DDL）
        ├── data.sql                           # 初期データ投入（PostgreSQL構文）
        ├── static/css/
        │   └── style.css                      # スタイルシート
        └── templates/
            ├── form.html                      # 顧客登録・編集フォーム
            ├── list.html                      # 顧客一覧
            ├── error.html                     # エラー画面
            └── account-plan/
                ├── show.html                  # アカウントプラン表示（マークダウンHTMLレンダリング）
                └── form.html                  # アカウントプラン登録・編集フォーム（マークダウンプレビュー付き）
```

### 画面遷移

```
顧客一覧 (list.html)
    │
    ├── [新規登録] ──→ 登録フォーム (form.html, isEdit=false)
    │                      │
    │                      ├── [保存] ──→ 顧客一覧
    │                      └── [キャンセル] ──→ 顧客一覧
    │
    ├── [編集] ──→ 編集フォーム (form.html, isEdit=true)
    │                 │
    │                 ├── [保存] ──→ 顧客一覧
    │                 └── [キャンセル] ──→ 顧客一覧
    │
    ├── [削除] ──→ 確認ダイアログ ──→ 顧客一覧
    │
    └── [プラン] ──→ アカウントプラン表示 (account-plan/show.html)
                        │
                        ├── [新規作成] ──→ 登録フォーム (account-plan/form.html, isEdit=false)
                        │                      │
                        │                      ├── [保存] ──→ アカウントプラン表示
                        │                      └── [キャンセル] ──→ アカウントプラン表示
                        │
                        ├── [編集] ──→ 編集フォーム (account-plan/form.html, isEdit=true)
                        │                 │
                        │                 ├── [保存] ──→ アカウントプラン表示
                        │                 └── [キャンセル] ──→ アカウントプラン表示
                        │
                        ├── [削除] ──→ 確認ダイアログ ──→ アカウントプラン表示
                        │
                        └── [顧客一覧に戻る] ──→ 顧客一覧
```

### 初期データ

アプリケーション起動時に、`src/main/resources/data.sql` により以下のサンプルデータが PostgreSQL に自動投入されます。IDは `customers_seq` シーケンスにより自動採番されます。

| 氏名 | メール | 電話番号 | 会社名 |
|------|--------|----------|--------|
| 田中太郎 | tanaka@example.com | 03-1234-5678 | 株式会社サンプル |
| 佐藤花子 | sato@example.com | 03-2345-6789 | テスト株式会社 |
| 鈴木一郎 | suzuki@example.com | 03-3456-7890 | デモ企業 |

## Goals/Non-Goals（目標/非目標）

### 目標

本アプリケーションの目標は以下の通りです。

Spring Boot + Thymeleafを使用したWebアプリケーション開発の基本パターンを示すこと、MVCアーキテクチャの実装例を提供すること、CRUD操作の実装方法を学習できるサンプルコードを提供すること、最小限の設定で即座に動作確認できる環境を提供することです。

### 非目標

本アプリケーションでは以下を意図的にスコープ外としています。

本番環境での使用、アカウントプランのデータベース永続化、認証・認可機能、入力バリデーション（サーバーサイド）、エラーハンドリングの詳細実装、ユニットテスト・統合テストの実装、REST API対応です。

## Cross-cutting concerns（横断的な懸念事項）

### セキュリティ

本アプリケーションはデモ用途であり、以下のセキュリティ機能は実装されていません。認証・認可、CSRF対策（Spring Securityなし）、入力値のサニタイズ。なお、顧客データは Spring Data JPA を介して PostgreSQL にアクセスしており、JPA がパラメータバインディングを行うため SQL インジェクションのリスクは低減されています。本番環境で使用する場合は、Spring Securityの導入と適切なセキュリティ設定が必要です。

### パフォーマンス

顧客データは PostgreSQL に永続化されていますが、全件取得（`findAll()`）が基本となっておりページネーションは未実装です。アカウントプランはインメモリストレージのため、大量データや再起動後のデータ保持には適していません。本番環境ではページネーションの実装およびアカウントプランのデータベース永続化が必要です。

### スケーラビリティ

顧客データは PostgreSQL に永続化されているため、複数インスタンスからのデータ共有が可能です。アカウントプランはインメモリストレージのため、スケールアウト時にはデータベースへの移行が必要です。

### 拡張性

顧客データは既に Spring Data JPA + PostgreSQL で永続化されています。アカウントプランの永続化が必要な場合は、同様に JPA エンティティとリポジトリを追加することで対応可能です。

## 変更履歴

| 日付 | 変更内容 | 担当者 |
|------|----------|--------|
| 2026-02-05 | 初版作成（リバースエンジニアリングによるドキュメント化） | Devin |
| 2026-02-17 | AccountPlanモデルおよびAccountPlanServiceの追加 | Devin |
| 2026-02-17 | AccountPlanControllerおよび画面テンプレートの追加 | Devin |
| 2026-02-17 | マークダウン入力プレビュー・HTMLレンダリング表示の追加 | Devin |
| 2026-03-04 | Oracle Database から PostgreSQL 16 への移行（インフラ・アプリケーション・ドキュメント） | Devin |
