CREATE TABLE IF NOT EXISTS jwks (ctx VARCHAR(10) NOT NULL, ts TIMESTAMP NOT NULL, jwks VARCHAR(80000) NOT NULL, PRIMARY KEY (ctx))

CREATE TABLE IF NOT EXISTS jwks_history (ts TIMESTAMP NOT NULL, ctx VARCHAR(10) NOT NULL, jwks VARCHAR(80000) NOT NULL, PRIMARY KEY (ts, ctx))

CREATE TABLE IF NOT EXISTS subject_sessions (id VARCHAR(80) NOT NULL, sub VARCHAR(250) NOT NULL, auth_ts TIMESTAMP NOT NULL, acr VARCHAR(100), amr VARCHAR(10) ARRAY, creation_ts TIMESTAMP NOT NULL, access_ts TIMESTAMP NOT NULL, ctx VARCHAR(10), jkt CHAR(43), fpt CHAR(43), max_life BIGINT DEFAULT -1, auth_life BIGINT DEFAULT -1, max_idle BIGINT DEFAULT -1, claims VARCHAR(4000), rps VARCHAR(100) ARRAY, data VARCHAR(4000), PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS subject_index (sub VARCHAR(500) NOT NULL, n INT NOT NULL, id VARCHAR(80) NOT NULL, PRIMARY KEY (sub, n))

CREATE TABLE IF NOT EXISTS pending_codes (code VARCHAR(80) NOT NULL, authz VARCHAR(30000) NOT NULL, acl BIGINT DEFAULT -1, PRIMARY KEY (code))

CREATE TABLE IF NOT EXISTS id_access_tokens (id VARCHAR(500) NOT NULL, sub VARCHAR(250) NOT NULL, act VARCHAR(250), cid VARCHAR(100) NOT NULL, scp VARCHAR(500) ARRAY, atl BIGINT DEFAULT -1, ats ENUM('PUBLIC','PAIRWISE') DEFAULT 'PUBLIC' NOT NULL, iss VARCHAR(250), iat TIMESTAMP NULL, aud VARCHAR(250) ARRAY, clm VARCHAR(100) ARRAY, cll VARCHAR(100) ARRAY, cld VARCHAR(4000), uip VARCHAR(4000), sik VARCHAR(80), dat VARCHAR(4000), oth VARCHAR(4000), cnf VARCHAR(150), PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS long_lived_authorizations (sub VARCHAR(250) NOT NULL, act VARCHAR(250) NOT NULL, cid VARCHAR(100) NOT NULL, scp VARCHAR(500) ARRAY, scs VARCHAR(500) ARRAY, irt BOOLEAN NOT NULL, rtl BIGINT DEFAULT 0, rtm BIGINT DEFAULT 0, rtr BOOLEAN, idr BOOLEAN, rts VARCHAR(255), rti TIMESTAMP NULL, atl BIGINT DEFAULT 0, ate VARCHAR(20) NOT NULL, atc BOOLEAN NOT NULL, ats ENUM('PUBLIC','PAIRWISE') DEFAULT 'PUBLIC' NOT NULL, iss VARCHAR(250), iat TIMESTAMP NULL, uat TIMESTAMP NULL, aud VARCHAR(250) ARRAY, clm VARCHAR(100) ARRAY, cll VARCHAR(100) ARRAY, cls VARCHAR(100) ARRAY, cld VARCHAR(4000), uip VARCHAR(4000), dat VARCHAR(4000), PRIMARY KEY (sub, act, cid))

CREATE TABLE IF NOT EXISTS revocation_journal (sub VARCHAR(250) NOT NULL, act VARCHAR(250) NOT NULL, cid VARCHAR(100) NOT NULL, ts TIMESTAMP NOT NULL, PRIMARY KEY (sub, act, cid))

CREATE TABLE IF NOT EXISTS expended_tokens (t_key VARCHAR(100) NOT NULL,type CHAR(2) NOT NULL,ts TIMESTAMP NOT NULL,tsp TIMESTAMP,dat VARCHAR(4000),etl BIGINT, PRIMARY KEY (t_key))

CREATE TABLE IF NOT EXISTS auth_sessions (sid VARCHAR(100) NOT NULL,session VARCHAR(20000) NOT NULL,iat TIMESTAMP NULL,asl BIGINT DEFAULT -1, PRIMARY KEY (sid))

CREATE TABLE IF NOT EXISTS consent_sessions (sid VARCHAR(100) NOT NULL,session VARCHAR(20000) NOT NULL,iat TIMESTAMP NULL,asl BIGINT DEFAULT -1, PRIMARY KEY (sid))

CREATE TABLE IF NOT EXISTS clients (client_id VARCHAR(100) NOT NULL,client_id_issued_at TIMESTAMP NULL,registration_access_token VARCHAR(1000),client_secret VARCHAR(500),client_secret_expires_at TIMESTAMP NULL,grant_types VARCHAR(100) ARRAY,response_types VARCHAR(50) ARRAY,redirect_uris VARCHAR(500) ARRAY,scope VARCHAR(500) ARRAY,token_endpoint_auth_method VARCHAR(100),token_endpoint_auth_signing_alg VARCHAR(10),jwks_uri VARCHAR(1000),jwks VARCHAR(50000),contacts VARCHAR(500) ARRAY,client_name VARCHAR(7500),client_uri VARCHAR(7500),logo_uri VARCHAR(7500),policy_uri VARCHAR(7500),tos_uri VARCHAR(7500),software_id VARCHAR(500),software_version VARCHAR(500),application_type VARCHAR(20),subject_type VARCHAR(20),sector_identifier_uri VARCHAR(1000),request_uris VARCHAR(500) ARRAY,request_object_signing_alg VARCHAR(10),request_object_encryption_alg VARCHAR(50),request_object_encryption_enc VARCHAR(50),authorization_signed_response_alg VARCHAR(10),authorization_encrypted_response_alg VARCHAR(50),authorization_encrypted_response_enc VARCHAR(50),require_pushed_authorization_requests BOOLEAN NOT NULL,tls_client_auth_subject_dn VARCHAR(200),tls_client_auth_san_dns VARCHAR(100),tls_client_auth_san_uri VARCHAR(100),tls_client_auth_san_ip VARCHAR(45),tls_client_auth_san_email VARCHAR(100),tls_client_certificate_bound_access_tokens BOOLEAN NOT NULL,dpop_bound_access_tokens BOOLEAN NOT NULL,code_challenge_method VARCHAR(10),id_token_signed_response_alg VARCHAR(10),id_token_encrypted_response_alg VARCHAR(50),id_token_encrypted_response_enc VARCHAR(50),userinfo_signed_response_alg VARCHAR(10),userinfo_encrypted_response_alg VARCHAR(50),userinfo_encrypted_response_enc VARCHAR(50),default_max_age BIGINT DEFAULT -1,require_auth_time BOOLEAN NOT NULL,default_acr_values VARCHAR(100) ARRAY,initiate_login_uri VARCHAR(1000),frontchannel_logout_uri VARCHAR(1000),frontchannel_logout_session_required BOOLEAN NOT NULL,backchannel_logout_uri VARCHAR(1000),backchannel_logout_session_required BOOLEAN NOT NULL,post_logout_redirect_uris VARCHAR(500) ARRAY,backchannel_token_delivery_mode VARCHAR(4), backchannel_client_notification_endpoint VARCHAR(1000),backchannel_authentication_request_signing_alg VARCHAR(10),backchannel_user_code_parameter BOOLEAN NOT NULL,data VARCHAR(10000),PRIMARY KEY (client_id))

CREATE TABLE IF NOT EXISTS federation_clients (entity_id VARCHAR(500) NOT NULL, n INT NOT NULL, anchor_id VARCHAR(500) NOT NULL, reg VARCHAR(20) NOT NULL, cid VARCHAR(100), iat TIMESTAMP NOT NULL, exp TIMESTAMP NOT NULL, misc VARCHAR(4000), PRIMARY KEY (entity_id, n))

CREATE TABLE IF NOT EXISTS client_jwks (client_id VARCHAR(100) NOT NULL,jwks VARCHAR(10000) NOT NULL,PRIMARY KEY (client_id))

CREATE TABLE IF NOT EXISTS request_jwts (uri VARCHAR(1000) NOT NULL,client_id VARCHAR(100) NOT NULL,clm VARCHAR(10000) NOT NULL,frg VARCHAR(100),iat TIMESTAMP NULL,rol BIGINT DEFAULT -1, PRIMARY KEY (uri))

