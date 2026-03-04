# Oracle → PostgreSQL マイグレーション（Spring Boot / Docker Compose）

このSKILLは、Oracle DB を利用している Spring Boot アプリを PostgreSQL に移行する際に、レビュー可能な粒度で安全に進めるための手順と注意点をまとめたものです。

## 基本方針（このリポジトリで採用した進め方）
- **フェーズを分けて** 変更する（レビューしやすさ優先）
  - フェーズ1: インフラ（Docker Compose / JDBC依存）
  - フェーズ2: アプリ（接続設定 / DDL-DML / JPAエンティティ）
  - フェーズ3: ドキュメント
- 各フェーズで **動作確認** を行い、必要なら証跡（ログ・スクリーンショット）を残す
- 各フェーズごとに **PRを分ける**（承認後に次へ）

## よくある落とし穴（このセッションで実際に遭遇）
### 1) 接続情報のハードコードがコミットをブロックする
- pre-commit / secret scan により `application.properties` の **ユーザー名・パスワード直書き** が検知され、コミットが失敗する場合がある
- **対策**: Spring Boot の環境変数プレースホルダーを使って外出しする

例:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/crm}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:crm_user}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 2) PostgreSQLの識別子は「小文字」前提で揃えると事故が減る
- PostgreSQLは **未クオート識別子を小文字化** する
- DDL/DML/JPAの `@Table` / `@SequenceGenerator` で **表記ゆれ（大文字・小文字）** があると、テーブルやシーケンスが見つからない原因になる
- **対策**: テーブル名・カラム名・シーケンス名を **小文字で統一**

### 3) `spring.sql.init.mode=always` と `data.sql` の再実行
- `schema.sql` を `IF NOT EXISTS` にすると、アプリ再起動時にも `data.sql` が流れ、
  **初期データが重複投入** される可能性がある
- **対策例**:
  - 初期投入後は `spring.sql.init.mode` を見直す（例: `never`）
  - `data.sql` を冪等化（ユニーク制約 + `ON CONFLICT DO NOTHING` 等）

## 変換の典型パターン（Oracle → PostgreSQL）
### DDL（schema.sql）
- `NUMBER(3)` → `INTEGER`
- `VARCHAR2(n)` → `VARCHAR(n)`
- `CREATE SEQUENCE CUSTOMERS_SEQ ...` → `CREATE SEQUENCE IF NOT EXISTS customers_seq ...`
- `CREATE TABLE CUSTOMERS ...` → `CREATE TABLE IF NOT EXISTS customers ...`

### DML（data.sql）
- `CUSTOMERS_SEQ.NEXTVAL` → `nextval('customers_seq')`

## JPA（エンティティ）
- テーブル名・シーケンス名をDB側と一致させる

例:
```java
@Entity
@Table(name = "customers")
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq")
  @SequenceGenerator(name = "customers_seq", sequenceName = "customers_seq", allocationSize = 1)
  private Long id;
}
```

## Docker Compose での接続（推奨）
- DBコンテナの認証情報は `.env`（コミットしない）で管理し、composeへ渡す
- アプリコンテナへは `SPRING_DATASOURCE_*` を環境変数で渡す
- `pg_isready` のヘルスチェック + `depends_on: condition: service_healthy` で起動順を安定化

例（抜粋）:
```yaml
postgres-db:
  image: postgres:16-alpine
  environment:
    POSTGRES_DB: ${POSTGRES_DB}
    POSTGRES_USER: ${POSTGRES_USER}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]

app:
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-db:5432/${POSTGRES_DB}
    SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
    SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
```

## 最小の動作確認チェックリスト
- [ ] `docker compose up -d --build` で app + DB が起動する
- [ ] appログに起動完了が出る（例: `Started ...`）
- [ ] 画面でCRUDが一通りできる（一覧/新規/更新/削除）
- [ ] ID採番（PostgreSQL sequence）が動いている
- [ ] DB移行と無関係な機能（インメモリ等）が壊れていない
