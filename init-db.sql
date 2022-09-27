/*
 Navicat Premium Data Transfer

 Source Server         : docman
 Source Server Type    : PostgreSQL
 Source Server Version : 120010
 Source Host           : 172.18.0.2:5432
 Source Catalog        : postgres
 Source Schema         : meta

 Target Server Type    : PostgreSQL
 Target Server Version : 120010
 File Encoding         : 65001

 Date: 27/09/2022 14:49:57
*/


-- ----------------------------
-- Sequence structure for area_cn_city_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "meta"."area_cn_city_code_seq";
CREATE SEQUENCE "meta"."area_cn_city_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "meta"."area_cn_city_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for area_cn_province_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "meta"."area_cn_province_code_seq";
CREATE SEQUENCE "meta"."area_cn_province_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "meta"."area_cn_province_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for area_cn_town_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "meta"."area_cn_town_code_seq";
CREATE SEQUENCE "meta"."area_cn_town_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "meta"."area_cn_town_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for area_street_district_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "meta"."area_street_district_code_seq";
CREATE SEQUENCE "meta"."area_street_district_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "meta"."area_street_district_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for area_cn_city
-- ----------------------------
DROP TABLE IF EXISTS "meta"."area_cn_city";
CREATE TABLE "meta"."area_cn_city" (
  "code" int4 NOT NULL DEFAULT nextval('"meta".area_cn_city_code_seq'::regclass),
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "provinceCode" int4 NOT NULL
)
;
ALTER TABLE "meta"."area_cn_city" OWNER TO "postgres";
COMMENT ON TABLE "meta"."area_cn_city" IS '省 - 城市';

-- ----------------------------
-- Table structure for area_cn_county
-- ----------------------------
DROP TABLE IF EXISTS "meta"."area_cn_county";
CREATE TABLE "meta"."area_cn_county" (
  "code" int4 NOT NULL DEFAULT nextval('"meta".area_street_district_code_seq'::regclass),
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "cityCode" int4 NOT NULL,
  "provinceCode" int4 NOT NULL
)
;
ALTER TABLE "meta"."area_cn_county" OWNER TO "postgres";
COMMENT ON TABLE "meta"."area_cn_county" IS '行政区划 - 中国 - 县级';

-- ----------------------------
-- Table structure for area_cn_province
-- ----------------------------
DROP TABLE IF EXISTS "meta"."area_cn_province";
CREATE TABLE "meta"."area_cn_province" (
  "code" int4 NOT NULL DEFAULT nextval('"meta".area_cn_province_code_seq'::regclass),
  "name" text COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "meta"."area_cn_province" OWNER TO "postgres";
COMMENT ON TABLE "meta"."area_cn_province" IS '行政区划 - 省级';

-- ----------------------------
-- Table structure for area_cn_town
-- ----------------------------
DROP TABLE IF EXISTS "meta"."area_cn_town";
CREATE TABLE "meta"."area_cn_town" (
  "code" int4 NOT NULL DEFAULT nextval('"meta".area_cn_town_code_seq'::regclass),
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "countyCode" int4 NOT NULL,
  "cityCode" int4 NOT NULL,
  "provinceCode" int4
)
;
ALTER TABLE "meta"."area_cn_town" OWNER TO "postgres";
COMMENT ON TABLE "meta"."area_cn_town" IS '行政区划 - 中国 - 镇街';

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "meta"."area_cn_city_code_seq"
OWNED BY "meta"."area_cn_city"."code";
SELECT setval('"meta"."area_cn_city_code_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "meta"."area_cn_province_code_seq"
OWNED BY "meta"."area_cn_province"."code";
SELECT setval('"meta"."area_cn_province_code_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "meta"."area_cn_town_code_seq"
OWNED BY "meta"."area_cn_town"."code";
SELECT setval('"meta"."area_cn_town_code_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "meta"."area_street_district_code_seq"
OWNED BY "meta"."area_cn_county"."code";
SELECT setval('"meta"."area_street_district_code_seq"', 1, false);

-- ----------------------------
-- Primary Key structure for table area_cn_city
-- ----------------------------
ALTER TABLE "meta"."area_cn_city" ADD CONSTRAINT "area_cn_city_pkey" PRIMARY KEY ("code");

-- ----------------------------
-- Primary Key structure for table area_cn_county
-- ----------------------------
ALTER TABLE "meta"."area_cn_county" ADD CONSTRAINT "area_street_district_pkey" PRIMARY KEY ("code");

-- ----------------------------
-- Primary Key structure for table area_cn_province
-- ----------------------------
ALTER TABLE "meta"."area_cn_province" ADD CONSTRAINT "area_cn_province_pkey" PRIMARY KEY ("code");

-- ----------------------------
-- Primary Key structure for table area_cn_town
-- ----------------------------
ALTER TABLE "meta"."area_cn_town" ADD CONSTRAINT "area_cn_town_pkey" PRIMARY KEY ("code");

/*
 Navicat Premium Data Transfer

 Source Server         : docman
 Source Server Type    : PostgreSQL
 Source Server Version : 120010
 Source Host           : 172.18.0.2:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120010
 File Encoding         : 65001

 Date: 27/09/2022 14:51:00
*/


-- ----------------------------
-- Type structure for inspectiontype
-- ----------------------------
DROP TYPE IF EXISTS "public"."inspectiontype";
CREATE TYPE "public"."inspectiontype" AS ENUM (
  'Acceptance',
  'State',
  'None',
  'Other'
);
ALTER TYPE "public"."inspectiontype" OWNER TO "postgres";

-- ----------------------------
-- Type structure for role
-- ----------------------------
DROP TYPE IF EXISTS "public"."role";
CREATE TYPE "public"."role" AS ENUM (
  'admin',
  'editor',
  'viewer'
);
ALTER TYPE "public"."role" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."user_id_seq";
CREATE SEQUENCE "public"."user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."user_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for clients
-- ----------------------------
DROP TABLE IF EXISTS "public"."clients";
CREATE TABLE "public"."clients" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "createdAt" timestamptz(6) DEFAULT now(),
  "updatedAt" timestamptz(6) DEFAULT now(),
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "comment" text COLLATE "pg_catalog"."default",
  "address" jsonb NOT NULL DEFAULT init_address()
)
;
ALTER TABLE "public"."clients" OWNER TO "postgres";
COMMENT ON TABLE "public"."clients" IS '委托单位管理';

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS "public"."device";
CREATE TABLE "public"."device" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "requester" varchar(128) COLLATE "pg_catalog"."default",
  "model" varchar(128) COLLATE "pg_catalog"."default",
  "equipmentName" varchar(128) COLLATE "pg_catalog"."default",
  "sampleId" varchar(128) COLLATE "pg_catalog"."default",
  "deviceNo" varchar(128) COLLATE "pg_catalog"."default",
  "manufacturer" varchar(128) COLLATE "pg_catalog"."default",
  "place" varchar(128) COLLATE "pg_catalog"."default",
  "equipment" varchar(128) COLLATE "pg_catalog"."default",
  "createTime" timestamptz(6) NOT NULL DEFAULT now(),
  "updateTime" timestamptz(6) NOT NULL DEFAULT now(),
  "createrId" int4,
  "comment" text COLLATE "pg_catalog"."default",
  "clientId" uuid,
  "address" jsonb NOT NULL DEFAULT init_address()
)
;
ALTER TABLE "public"."device" OWNER TO "postgres";
COMMENT ON COLUMN "public"."device"."id" IS '设备ID';
COMMENT ON COLUMN "public"."device"."requester" IS '委托单位';
COMMENT ON COLUMN "public"."device"."model" IS '设备型号';
COMMENT ON COLUMN "public"."device"."equipmentName" IS '设备名称';
COMMENT ON COLUMN "public"."device"."sampleId" IS '样品标识';
COMMENT ON COLUMN "public"."device"."deviceNo" IS '设备编号';
COMMENT ON COLUMN "public"."device"."manufacturer" IS '制造商';
COMMENT ON COLUMN "public"."device"."place" IS '设备场所';
COMMENT ON COLUMN "public"."device"."equipment" IS '检测仪器';
COMMENT ON COLUMN "public"."device"."createTime" IS '创建时间';
COMMENT ON COLUMN "public"."device"."updateTime" IS '更新时间';
COMMENT ON COLUMN "public"."device"."createrId" IS '创建者id';
COMMENT ON COLUMN "public"."device"."comment" IS '备注信息';
COMMENT ON COLUMN "public"."device"."clientId" IS '委托单位Id';
COMMENT ON COLUMN "public"."device"."address" IS '检测地址';

-- ----------------------------
-- Table structure for equipment_enum
-- ----------------------------
DROP TABLE IF EXISTS "public"."equipment_enum";
CREATE TABLE "public"."equipment_enum" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "name" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "displayName" text COLLATE "pg_catalog"."default",
  "comment" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."equipment_enum" OWNER TO "postgres";
COMMENT ON TABLE "public"."equipment_enum" IS '设备种类举例';

-- ----------------------------
-- Table structure for global_const
-- ----------------------------
DROP TABLE IF EXISTS "public"."global_const";
CREATE TABLE "public"."global_const" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "name" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "value" numeric NOT NULL,
  "comment" text COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."global_const" OWNER TO "postgres";
COMMENT ON COLUMN "public"."global_const"."name" IS '常量名';
COMMENT ON COLUMN "public"."global_const"."value" IS '常量值';
COMMENT ON COLUMN "public"."global_const"."comment" IS '备注';
COMMENT ON TABLE "public"."global_const" IS '全局常量';

-- ----------------------------
-- Table structure for inspection_item_enum
-- ----------------------------
DROP TABLE IF EXISTS "public"."inspection_item_enum";
CREATE TABLE "public"."inspection_item_enum" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "displayName" text COLLATE "pg_catalog"."default",
  "comment" text COLLATE "pg_catalog"."default",
  "inspectionCondition" jsonb NOT NULL,
  "acceptanceRequire" text COLLATE "pg_catalog"."default",
  "stateRequire" text COLLATE "pg_catalog"."default",
  "formula" text COLLATE "pg_catalog"."default",
  "inputName" text COLLATE "pg_catalog"."default",
  "inputCount" int4 NOT NULL DEFAULT 1,
  "inputUnit" varchar COLLATE "pg_catalog"."default",
  "outputName" text COLLATE "pg_catalog"."default" NOT NULL,
  "outputUnit" varchar COLLATE "pg_catalog"."default",
  "equipment_id" uuid,
  "name" varchar COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."inspection_item_enum" OWNER TO "postgres";
COMMENT ON COLUMN "public"."inspection_item_enum"."displayName" IS '检测项目名';
COMMENT ON COLUMN "public"."inspection_item_enum"."comment" IS '检测项目备注';
COMMENT ON COLUMN "public"."inspection_item_enum"."inspectionCondition" IS '预设检测条件（加载因素、预设值）';
COMMENT ON COLUMN "public"."inspection_item_enum"."acceptanceRequire" IS '指标要求 - 验收检测';
COMMENT ON COLUMN "public"."inspection_item_enum"."stateRequire" IS '指标要求 - 状态检测';
COMMENT ON COLUMN "public"."inspection_item_enum"."formula" IS '公式';
COMMENT ON COLUMN "public"."inspection_item_enum"."inputName" IS '输入值名称';
COMMENT ON COLUMN "public"."inspection_item_enum"."inputCount" IS '输入值数量';
COMMENT ON COLUMN "public"."inspection_item_enum"."inputUnit" IS '输入值单位';
COMMENT ON COLUMN "public"."inspection_item_enum"."outputName" IS '输出值名称';
COMMENT ON COLUMN "public"."inspection_item_enum"."outputUnit" IS '输出值单位';
COMMENT ON TABLE "public"."inspection_item_enum" IS '检测项目枚举';

-- ----------------------------
-- Table structure for inspection_report
-- ----------------------------
DROP TABLE IF EXISTS "public"."inspection_report";
CREATE TABLE "public"."inspection_report" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "equipmentName" text COLLATE "pg_catalog"."default" DEFAULT '设备名'::text,
  "equipmentRequester" text COLLATE "pg_catalog"."default" DEFAULT '委托单位'::text,
  "equipmentSampleId" text COLLATE "pg_catalog"."default" DEFAULT '样品标识'::text,
  "equipmentModel" text COLLATE "pg_catalog"."default" DEFAULT '设备型号'::text,
  "equipmentManufacturer" text COLLATE "pg_catalog"."default" DEFAULT '制造厂商'::text,
  "equipmentSite" text COLLATE "pg_catalog"."default" DEFAULT '设备场所'::text,
  "inspectionBasis" text COLLATE "pg_catalog"."default" DEFAULT '检测依据'::text,
  "inspectionInstrument" text COLLATE "pg_catalog"."default" DEFAULT '检测仪器'::text,
  "createAt" timestamptz(6) DEFAULT now(),
  "creatorId" int4,
  "updatedAt" timestamptz(6) DEFAULT now(),
  "equipmentCode" varchar COLLATE "pg_catalog"."default",
  "inspectionDate" timestamptz(6),
  "inspectionAddress" jsonb NOT NULL DEFAULT init_address(),
  "serialNumber" jsonb DEFAULT init_sn(),
  "inspectionItem" jsonb NOT NULL DEFAULT init_inspectiontype()
)
;
ALTER TABLE "public"."inspection_report" OWNER TO "postgres";
COMMENT ON COLUMN "public"."inspection_report"."id" IS '主键';
COMMENT ON COLUMN "public"."inspection_report"."equipmentName" IS '设备名称';
COMMENT ON COLUMN "public"."inspection_report"."equipmentRequester" IS '委托单位';
COMMENT ON COLUMN "public"."inspection_report"."equipmentSampleId" IS '样品标识';
COMMENT ON COLUMN "public"."inspection_report"."equipmentModel" IS '设备型号';
COMMENT ON COLUMN "public"."inspection_report"."equipmentManufacturer" IS '制造厂商';
COMMENT ON COLUMN "public"."inspection_report"."equipmentSite" IS '设备场所';
COMMENT ON COLUMN "public"."inspection_report"."inspectionBasis" IS '检测依据';
COMMENT ON COLUMN "public"."inspection_report"."inspectionInstrument" IS '检测仪器';
COMMENT ON COLUMN "public"."inspection_report"."createAt" IS '创建时间';
COMMENT ON COLUMN "public"."inspection_report"."creatorId" IS '创建者id';
COMMENT ON COLUMN "public"."inspection_report"."equipmentCode" IS '设备编号';
COMMENT ON COLUMN "public"."inspection_report"."inspectionDate" IS '检测日期';
COMMENT ON COLUMN "public"."inspection_report"."inspectionAddress" IS '检测地址';
COMMENT ON COLUMN "public"."inspection_report"."serialNumber" IS '序列号';
COMMENT ON COLUMN "public"."inspection_report"."inspectionItem" IS '检测类型';
COMMENT ON TABLE "public"."inspection_report" IS '设备检验检测报告';

-- ----------------------------
-- Table structure for inspection_report_item
-- ----------------------------
DROP TABLE IF EXISTS "public"."inspection_report_item";
CREATE TABLE "public"."inspection_report_item" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "displayName" text COLLATE "pg_catalog"."default" NOT NULL,
  "condition" jsonb DEFAULT jsonb_build_object(),
  "result" jsonb DEFAULT jsonb_build_object(),
  "requirementAcceptance" text COLLATE "pg_catalog"."default",
  "requirementState" text COLLATE "pg_catalog"."default",
  "conclusion" text COLLATE "pg_catalog"."default",
  "reportId" uuid,
  "inputValues" jsonb DEFAULT jsonb_build_object(),
  "name" varchar COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."inspection_report_item" OWNER TO "postgres";
COMMENT ON COLUMN "public"."inspection_report_item"."id" IS '字段id';
COMMENT ON COLUMN "public"."inspection_report_item"."displayName" IS '检测项名';
COMMENT ON COLUMN "public"."inspection_report_item"."condition" IS '检测条件';
COMMENT ON COLUMN "public"."inspection_report_item"."result" IS '检测结果';
COMMENT ON COLUMN "public"."inspection_report_item"."requirementAcceptance" IS '指标要求：验收检测';
COMMENT ON COLUMN "public"."inspection_report_item"."requirementState" IS '指标要求：状态检测';
COMMENT ON COLUMN "public"."inspection_report_item"."conclusion" IS '结论';
COMMENT ON COLUMN "public"."inspection_report_item"."inputValues" IS '输入的值，可能有多个，可能有多组';
COMMENT ON COLUMN "public"."inspection_report_item"."name" IS '检测项名称，与 inspection_item_enum 内的对应';
COMMENT ON TABLE "public"."inspection_report_item" IS '检测项，全扔这里，不再JSON';

-- ----------------------------
-- Table structure for report_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."report_file";
CREATE TABLE "public"."report_file" (
  "id" uuid NOT NULL DEFAULT gen_random_uuid(),
  "reportNo" varchar COLLATE "pg_catalog"."default",
  "params" jsonb NOT NULL,
  "file" varchar COLLATE "pg_catalog"."default",
  "createTime" timestamp(6) NOT NULL DEFAULT now(),
  "updateTime" timestamp(6) NOT NULL DEFAULT now()
)
;
ALTER TABLE "public"."report_file" OWNER TO "postgres";

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS "public"."user";
CREATE TABLE "public"."user" (
  "id" int4 NOT NULL DEFAULT nextval('user_id_seq'::regclass),
  "username" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "role" "public"."role" NOT NULL DEFAULT 'viewer'::role,
  "displayname" text COLLATE "pg_catalog"."default" DEFAULT 'Untitled User'::text,
  "avatar" text COLLATE "pg_catalog"."default" DEFAULT 'https://avatars.dicebear.com/v2/avataaars/89e280ae430cad55e5c08e09ebe369a8.svg'::text
)
;
ALTER TABLE "public"."user" OWNER TO "postgres";

-- ----------------------------
-- Function structure for armor
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."armor"(bytea);
CREATE OR REPLACE FUNCTION "public"."armor"(bytea)
  RETURNS "pg_catalog"."text" AS '$libdir/pgcrypto', 'pg_armor'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."armor"(bytea) OWNER TO "postgres";

-- ----------------------------
-- Function structure for armor
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."armor"(bytea, _text, _text);
CREATE OR REPLACE FUNCTION "public"."armor"(bytea, _text, _text)
  RETURNS "pg_catalog"."text" AS '$libdir/pgcrypto', 'pg_armor'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."armor"(bytea, _text, _text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for crypt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."crypt"(text, text);
CREATE OR REPLACE FUNCTION "public"."crypt"(text, text)
  RETURNS "pg_catalog"."text" AS '$libdir/pgcrypto', 'pg_crypt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."crypt"(text, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for dearmor
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."dearmor"(text);
CREATE OR REPLACE FUNCTION "public"."dearmor"(text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_dearmor'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."dearmor"(text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for decrypt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."decrypt"(bytea, bytea, text);
CREATE OR REPLACE FUNCTION "public"."decrypt"(bytea, bytea, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_decrypt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."decrypt"(bytea, bytea, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for decrypt_iv
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."decrypt_iv"(bytea, bytea, bytea, text);
CREATE OR REPLACE FUNCTION "public"."decrypt_iv"(bytea, bytea, bytea, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_decrypt_iv'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."decrypt_iv"(bytea, bytea, bytea, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for digest
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."digest"(bytea, text);
CREATE OR REPLACE FUNCTION "public"."digest"(bytea, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_digest'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."digest"(bytea, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for digest
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."digest"(text, text);
CREATE OR REPLACE FUNCTION "public"."digest"(text, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_digest'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."digest"(text, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for encrypt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."encrypt"(bytea, bytea, text);
CREATE OR REPLACE FUNCTION "public"."encrypt"(bytea, bytea, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_encrypt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."encrypt"(bytea, bytea, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for encrypt_iv
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."encrypt_iv"(bytea, bytea, bytea, text);
CREATE OR REPLACE FUNCTION "public"."encrypt_iv"(bytea, bytea, bytea, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_encrypt_iv'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."encrypt_iv"(bytea, bytea, bytea, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for gen_random_bytes
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gen_random_bytes"(int4);
CREATE OR REPLACE FUNCTION "public"."gen_random_bytes"(int4)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_random_bytes'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."gen_random_bytes"(int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for gen_random_uuid
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gen_random_uuid"();
CREATE OR REPLACE FUNCTION "public"."gen_random_uuid"()
  RETURNS "pg_catalog"."uuid" AS '$libdir/pgcrypto', 'pg_random_uuid'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."gen_random_uuid"() OWNER TO "postgres";

-- ----------------------------
-- Function structure for gen_salt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gen_salt"(text, int4);
CREATE OR REPLACE FUNCTION "public"."gen_salt"(text, int4)
  RETURNS "pg_catalog"."text" AS '$libdir/pgcrypto', 'pg_gen_salt_rounds'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."gen_salt"(text, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for gen_salt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gen_salt"(text);
CREATE OR REPLACE FUNCTION "public"."gen_salt"(text)
  RETURNS "pg_catalog"."text" AS '$libdir/pgcrypto', 'pg_gen_salt'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."gen_salt"(text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hmac
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hmac"(bytea, bytea, text);
CREATE OR REPLACE FUNCTION "public"."hmac"(bytea, bytea, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_hmac'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."hmac"(bytea, bytea, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hmac
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hmac"(text, text, text);
CREATE OR REPLACE FUNCTION "public"."hmac"(text, text, text)
  RETURNS "pg_catalog"."bytea" AS '$libdir/pgcrypto', 'pg_hmac'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."hmac"(text, text, text) OWNER TO "postgres";

-- ----------------------------
-- Function structure for init_address
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."init_address"();
CREATE OR REPLACE FUNCTION "public"."init_address"()
  RETURNS "pg_catalog"."jsonb" AS $BODY$ 
        BEGIN
            RETURN '{"province": null, "city": null, "county": null, "town": null, "detail": "" }'::jsonb;
        END;
    $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "public"."init_address"() OWNER TO "postgres";

-- ----------------------------
-- Function structure for init_inspectiontype
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."init_inspectiontype"();
CREATE OR REPLACE FUNCTION "public"."init_inspectiontype"()
  RETURNS "pg_catalog"."jsonb" AS $BODY$ 
        BEGIN
            RETURN jsonb_build_object('type', 'None'::inspectiontype, 'text', '');
        END;
    $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "public"."init_inspectiontype"() OWNER TO "postgres";

-- ----------------------------
-- Function structure for init_sn
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."init_sn"();
CREATE OR REPLACE FUNCTION "public"."init_sn"()
  RETURNS "pg_catalog"."jsonb" AS $BODY$ 
        BEGIN
            RETURN jsonb_build_object('prefix', 'FYS', 'year', to_char(current_date, 'yyyy'), 'number', '');
        END;
    $BOD