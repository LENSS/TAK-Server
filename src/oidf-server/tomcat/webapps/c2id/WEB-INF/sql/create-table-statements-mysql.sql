CREATE TABLE IF NOT EXISTS jwks (ctx VARCHAR(10) NOT NULL, ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP, jwks JSON NOT NULL, PRIMARY KEY (ctx)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS jwks_history (ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ctx VARCHAR(10) NOT NULL, jwks JSON NOT NULL, PRIMARY KEY (ts, ctx)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS subject_sessions (id VARCHAR(80) NOT NULL, sub VARCHAR(250) NOT NULL, auth_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP, acr VARCHAR(100), amr JSON, creation_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP, access_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ctx VARCHAR(10), jkt BINARY(32), fpt BINARY(32), max_life BIGINT DEFAULT -1, auth_life BIGINT DEFAULT -1, max_idle BIGINT DEFAULT -1, claims JSON, rps JSON, data JSON, PRIMARY KEY (id)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS subject_index (sub VARCHAR(500) NOT NULL, n INT NOT NULL, id VARCHAR(80) NOT NULL, PRIMARY KEY (sub, n)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS pending_codes (code VARCHAR(80) NOT NULL, authz TEXT NOT NULL, acl BIGINT DEFAULT -1, PRIMARY KEY (code)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS id_access_tokens (id VARCHAR(500) NOT NULL, sub VARCHAR(250) NOT NULL, act VARCHAR(250), cid VARCHAR(100) NOT NULL, scp JSON, atl BIGINT DEFAULT -1, ats ENUM('PUBLIC','PAIRWISE') NOT NULL DEFAULT 'PUBLIC', iss VARCHAR(250), iat TIMESTAMP NULL, aud JSON, clm JSON, cll JSON, cld JSON, uip JSON, sik VARCHAR(80), dat JSON, oth JSON, cnf JSON, PRIMARY KEY (id)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS long_lived_authorizations (sub VARCHAR(250) NOT NULL, act VARCHAR(250) NOT NULL, cid VARCHAR(100) NOT NULL, scp JSON, scs JSON, irt BOOLEAN NOT NULL, rtl BIGINT DEFAULT 0, rtm BIGINT DEFAULT 0, rtr BOOLEAN, idr BOOLEAN, rts VARCHAR(255), rti TIMESTAMP NULL, atl BIGINT DEFAULT 0, ate VARCHAR(20) NOT NULL, atc BOOLEAN NOT NULL, ats ENUM('PUBLIC','PAIRWISE') NOT NULL DEFAULT 'PUBLIC', iss VARCHAR(250), iat TIMESTAMP NULL, uat TIMESTAMP NULL, aud JSON, clm JSON, cll JSON, cls JSON, cld JSON, uip JSON, dat JSON, PRIMARY KEY (sub, act, cid)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS revocation_journal (sub VARCHAR(250) NOT NULL, act VARCHAR(250) NOT NULL, cid VARCHAR(100) NOT NULL, ts TIMESTAMP NOT NULL, PRIMARY KEY (sub, act, cid)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS expended_tokens (t_key VARCHAR(100) NOT NULL,type CHAR(2) NOT NULL,ts TIMESTAMP NOT NULL,tsp TIMESTAMP,dat JSON,etl BIGINT, PRIMARY KEY (t_key)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS auth_sessions (sid VARCHAR(100) NOT NULL,session VARCHAR(20000) NOT NULL,iat TIMESTAMP NULL,asl BIGINT DEFAULT -1, PRIMARY KEY (sid)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS consent_sessions (sid VARCHAR(100) NOT NULL,session VARCHAR(20000) NOT NULL,iat TIMESTAMP NULL,asl BIGINT DEFAULT -1, PRIMARY KEY (sid)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS clients (client_id VARCHAR(100) NOT NULL,client_id_issued_at TIMESTAMP NULL,registration_access_token VARCHAR(1000),client_secret VARCHAR(500),client_secret_expires_at TIMESTAMP NULL,grant_types JSON,response_types JSON,redirect_uris JSON,scope JSON,token_endpoint_auth_method VARCHAR(100),token_endpoint_auth_signing_alg VARCHAR(10),jwks_uri VARCHAR(1000),jwks JSON,contacts JSON,client_name JSON,client_uri JSON,logo_uri JSON,policy_uri JSON,tos_uri JSON,software_id VARCHAR(500),software_version VARCHAR(500),application_type VARCHAR(20),subject_type VARCHAR(20),sector_identifier_uri VARCHAR(1000),request_uris JSON,request_object_signing_alg VARCHAR(10),request_object_encryption_alg VARCHAR(50),request_object_encryption_enc VARCHAR(50),authorization_signed_response_alg VARCHAR(10),authorization_encrypted_response_alg VARCHAR(50),authorization_encrypted_response_enc VARCHAR(50),require_pushed_authorization_requests BOOLEAN NOT NULL,tls_client_auth_subject_dn VARCHAR(200),tls_client_auth_san_dns VARCHAR(100),tls_client_auth_san_uri VARCHAR(100),tls_client_auth_san_ip VARCHAR(45),tls_client_auth_san_email VARCHAR(100),tls_client_certificate_bound_access_tokens BOOLEAN NOT NULL,dpop_bound_access_tokens BOOLEAN NOT NULL,code_challenge_method VARCHAR(10),id_token_signed_response_alg VARCHAR(10),id_token_encrypted_response_alg VARCHAR(50),id_token_encrypted_response_enc VARCHAR(50),userinfo_signed_response_alg VARCHAR(10),userinfo_encrypted_response_alg VARCHAR(50),userinfo_encrypted_response_enc VARCHAR(50),default_max_age BIGINT DEFAULT -1,require_auth_time BOOLEAN NOT NULL,default_acr_values JSON,initiate_login_uri VARCHAR(1000),frontchannel_logout_uri VARCHAR(1000),frontchannel_logout_session_required BOOLEAN NOT NULL,backchannel_logout_uri VARCHAR(1000),backchannel_logout_session_required BOOLEAN NOT NULL,post_logout_redirect_uris JSON,backchannel_token_delivery_mode VARCHAR(4), backchannel_client_notification_endpoint VARCHAR(1000),backchannel_authentication_request_signing_alg VARCHAR(10),backchannel_user_code_parameter BOOLEAN NOT NULL,data JSON,PRIMARY KEY (client_id)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS federation_clients (entity_id VARCHAR(500) NOT NULL, n INT NOT NULL, anchor_id VARCHAR(500) NOT NULL, reg VARCHAR(20) NOT NULL, cid VARCHAR(100), iat TIMESTAMP DEFAULT CURRENT_TIMESTAMP, exp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, misc JSON, PRIMARY KEY (entity_id, n)) CHARACTER SET = utf8

CREATE TABLE IF NOT EXISTS client_jwks (client_id VARCHAR(100) NOT NULL,jwks VARCHAR(10000) NOT NULL,PRIMARY KEY (client_id)) CHARACTER SET utf8

CREATE TABLE IF NOT EXISTS request_jwts (uri VARCHAR(1000) NOT NULL,client_id VARCHAR(100) NOT NULL,clm VARCHAR(10000),frg VARCHAR(100),iat TIMESTAMP NULL,rol BIGINT DEFAULT -1, PRIMARY KEY (uri)) CHARACTER SET utf8

