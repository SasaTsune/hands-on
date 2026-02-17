# Demo Java CRM - Design Document

## Context/Background（背景）

本ドキュメントは、Demo Java CRMアプリケーションの設計と実装を記録するものです。このアプリケーションは、Spring Boot + Thymeleafを使用した顧客管理システムのデモンストレーションとして開発されました。

開発の目的は、Spring Bootを用いたWebアプリケーション開発の基本的なパターン（MVC、CRUD操作、テンプレートエンジン）を学習・理解するためのサンプルコードを提供することです。本番環境での使用を想定したものではなく、教育・学習目的のシンプルな実装となっています。

データストレージにはインメモリ（ArrayList）を採用しており、アプリケーション再起動時にデータはリセットされます。これは意図的な設計であり、データベース設定なしで即座に動作確認できることを優先しています。

## 機能一覧

本アプリケーションは以下の機能を提供します。

### アカウントプラン管理機能

顧客に紐づくアカウントプラン情報を管理します。アカウントプランの内容はマークダウン形式で記述できます。`AccountPlanService`により、顧客IDに紐づくプランの取得、新規登録、更新、削除の操作が可能です。データはインメモリ（ArrayList）で管理されます。`AccountPlanController`が画面遷移とCRUD操作のエンドポイントを提供します。

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
│  - ID生成（AtomicLong）                                       │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│          (ArrayList<Customer>, ArrayList<AccountPlan>)        │
│  - インメモリデータストレージ                                  │
└─────────────────────────────────────────────────────────────┘
```

### データモデル

#### 顧客情報（Customer）

顧客情報は`Customer`クラスで表現されます。

| フィールド | 型 | 必須 | 説明 |
|-----------|------|------|------|
| id | Long | 自動生成 | 一意識別子。AtomicLongによる連番 |
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
| ビルドツール | Maven | - |
| Webサーバー | 組み込みTomcat | Spring Boot管理 |

### ディレクトリ構成

```
demo-java-crm/
├── pom.xml                                    # Maven設定
├── README.md                                  # プロジェクト説明
├── docs/
│   └── DesignDoc.md                           # 本ドキュメント
└── src/main/
    ├── java/com/example/crm/
    │   ├── CrmApplication.java                # アプリケーション起動クラス
    │   ├── controller/
    │   │   ├── CustomerController.java        # 顧客コントローラー
    │   │   └── AccountPlanController.java     # アカウントプランコントローラー
    │   ├── model/
    │   │   ├── Customer.java                  # 顧客ドメインモデル
    │   │   └── AccountPlan.java               # アカウントプランドメインモデル
    │   └── service/
    │       ├── CustomerService.java           # 顧客ビジネスロジック
    │       └── AccountPlanService.java        # アカウントプランビジネスロジック
    └── resources/
        ├── application.properties             # アプリケーション設定
        ├── static/css/
        │   └── style.css                      # スタイルシート
        └── templates/
            ├── form.html                      # 顧客登録・編集フォーム
            ├── list.html                      # 顧客一覧
            └── account-plan/
                ├── show.html                  # アカウントプラン表示
                └── form.html                  # アカウントプラン登録・編集フォーム
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

アプリケーション起動時に、`CustomerService`のコンストラクタで以下のサンプルデータが自動登録されます。

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

本番環境での使用、データの永続化（データベース連携）、認証・認可機能、入力バリデーション（サーバーサイド）、エラーハンドリングの詳細実装、ユニットテスト・統合テストの実装、REST API対応です。

## Cross-cutting concerns（横断的な懸念事項）

### セキュリティ

本アプリケーションはデモ用途であり、以下のセキュリティ機能は実装されていません。認証・認可、CSRF対策（Spring Securityなし）、入力値のサニタイズ、SQLインジェクション対策（データベース未使用のため該当なし）。本番環境で使用する場合は、Spring Securityの導入と適切なセキュリティ設定が必要です。

### パフォーマンス

インメモリストレージを使用しているため、大量データの処理には適していません。`findAll()`メソッドは全データをコピーして返却するため、データ量が増加するとメモリ使用量が増大します。本番環境ではデータベースとページネーションの実装が必要です。

### スケーラビリティ

インメモリストレージのため、複数インスタンスでのデータ共有ができません。スケールアウトが必要な場合は、外部データベースへの移行が必須です。

### 拡張性

データの永続化が必要な場合は、Spring Data JPAとH2/PostgreSQL等のデータベースを導入することで対応可能です。READMEにH2データベース導入の手順が記載されています。

## 変更履歴

| 日付 | 変更内容 | 担当者 |
|------|----------|--------|
| 2026-02-05 | 初版作成（リバースエンジニアリングによるドキュメント化） | Devin |
| 2026-02-17 | AccountPlanモデルおよびAccountPlanServiceの追加 | Devin |
| 2026-02-17 | AccountPlanControllerおよび画面テンプレートの追加 | Devin |
