CREATE TABLE jwks (ctx VARCHAR2(10) NOT NULL, ts TIMESTAMP(0) NOT NULL, jwks CLOB NOT NULL, PRIMARY KEY (ctx))

CREATE TABLE jwks_history (ts TIMESTAMP(0) NOT NULL, ctx VARCHAR2(10) NOT NULL, jwks CLOB NOT NULL, PRIMARY KEY (ts, ctx))

CREATE TABLE subject_sessions (id VARCHAR2(80) NOT NULL, sub VARCHAR2(250) NOT NULL, auth_ts TIMESTAMP(0) NOT NULL, acr VARCHAR2(100), amr VARCHAR2(200), creation_ts TIMESTAMP(0) NOT NULL, access_ts TIMESTAMP(0) NOT NULL, ctx VARCHAR2(10), jkt RAW(32), fpt RAW(32), max_life NUMBER(8,0) DEFAULT -1, auth_life NUMBER(8,0) DEFAULT -1, max_idle NUMBER(8,0) DEFAULT -1, claims NCLOB, rps VARCHAR2(2000), data NCLOB, PRIMARY KEY (id))

CREATE TABLE subject_index (sub VARCHAR2(500) NOT NULL, n NUMBER(2,0) NOT NULL, id VARCHAR2(80) NOT NULL, PRIMARY KEY (sub, n))

CREATE TABLE pending_codes (code VARCHAR2(80) NOT NULL, authz NCLOB NOT NULL, acl NUMBER(12,0) DEFAULT -1, PRIMARY KEY (code))

CREATE TABLE id_access_tokens (id VARCHAR2(500) NOT NULL, sub VARCHAR2(250) NOT NULL, act VARCHAR2(250), cid VARCHAR2(100) NOT NULL, scp VARCHAR2(2000), atl NUMBER(10,0) DEFAULT -1, ats VARCHAR2(10) DEFAULT 'PUBLIC', iss VARCHAR2(250), iat TIMESTAMP(0) NULL, aud VARCHAR2(250), clm NVARCHAR2(2000), cll VARCHAR2(100), cld NCLOB, uip NCLOB, sik VARCHAR2(80), dat NCLOB, oth NCLOB, cnf VARCHAR2(150), PRIMARY KEY (id))

CREATE TABLE long_lived_authorizations (sub VARCHAR2(250) NOT NULL, act VARCHAR2(250) NOT NULL, cid VARCHAR2(100) NOT NULL, scp VARCHAR2(2000), scs VARCHAR2(2000), irt NUMBER(1,0) NOT NULL, rtl NUMBER(10,0) DEFAULT 0, rtm NUMBER(10,0) DEFAULT 0, rtr NUMBER(1,0), idr NUMBER(1,0), rts VARCHAR2(100), rti TIMESTAMP(0) NULL, atl NUMBER(10,0) DEFAULT 0, ate VARCHAR2(20) NOT NULL, atc NUMBER(1,0) NOT NULL, ats VARCHAR2(10) DEFAULT 'PUBLIC', iss VARCHAR2(250), iat TIMESTAMP(0) NULL, uat TIMESTAMP(0) NULL, aud VARCHAR2(500), clm NVARCHAR2(2000), cll VARCHAR2(100), cls NVARCHAR2(2000), cld NCLOB, uip NCLOB, dat NCLOB, PRIMARY KEY (sub, act, cid))

CREATE TABLE revocation_journal (sub VARCHAR2(250) NOT NULL, act VARCHAR2(250) NOT NULL, cid VARCHAR2(100) NOT NULL, ts TIMESTAMP(0) NOT NULL, PRIMARY KEY (sub, act, cid))

CREATE TABLE expended_tokens (t_key VARCHAR2(100) NOT NULL,type CHAR(2) NOT NULL,ts TIMESTAMP(0) NOT NULL,tsp TIMESTAMP(0),dat VARCHAR2(2000),etl NUMBER(12,0), PRIMARY KEY (t_key))

CREATE TABLE auth_sessions (sid VARCHAR2(100) NOT NULL,session_data NCLOB NOT NULL,iat TIMESTAMP(0) NULL,asl NUMBER(12,0) DEFAULT -1,PRIMARY KEY (sid))

CREATE TABLE consent_sessions (sid VARCHAR2(100) NOT NULL,session_data NCLOB NOT NULL,iat TIMESTAMP(0) NULL,asl NUMBER(12,0) DEFAULT -1,PRIMARY KEY (sid))

CREATE TABLE clients (client_id VARCHAR2(80) NOT NULL,client_id_issued_at TIMESTAMP(0) NULL,registration_access_token VARCHAR2(200),client_secret VARCHAR2(200),client_secret_expires_at TIMESTAMP(0) NULL,grant_types VARCHAR2(200),response_types VARCHAR2(200),redirect_uris CLOB,scope VARCHAR2(2000),token_endpoint_auth_method VARCHAR2(100),token_endpoint_auth_signing_alg VARCHAR2(20),jwks_uri VARCHAR2(200),jwks CLOB,contacts VARCHAR2(200),client_name NVARCHAR2(2000),client_uri VARCHAR2(1000),logo_uri VARCHAR2(1000),policy_uri VARCHAR2(1000),tos_uri VARCHAR2(1000),software_id VARCHAR2(100),software_version VARCHAR2(100),application_type VARCHAR2(20),subject_type VARCHAR2(20),sector_identifier_uri VARCHAR2(200),request_uris VARCHAR2(1000),request_object_signing_alg VARCHAR2(10),request_object_encryption_alg VARCHAR2(50),request_object_encryption_enc VARCHAR2(50),authorization_signed_response_alg VARCHAR2(10),authorization_encrypted_response_alg VARCHAR2(50),authorization_encrypted_response_enc VARCHAR2(50),require_pushed_authorization_requests NUMBER(1,0) NOT NULL,tls_client_auth_subject_dn VARCHAR2(200),tls_client_auth_san_dns VARCHAR2(100),tls_client_auth_san_uri VARCHAR2(100),tls_client_auth_san_ip VARCHAR2(45),tls_client_auth_san_email VARCHAR2(100),tls_client_certificate_bound_access_tokens NUMBER(1,0) NOT NULL,dpop_bound_access_tokens NUMBER(1,0) NOT NULL,code_challenge_method VARCHAR2(10),id_token_signed_response_alg VARCHAR2(10),id_token_encrypted_response_alg VARCHAR2(50),id_token_encrypted_response_enc VARCHAR2(50),userinfo_signed_response_alg VARCHAR2(10),userinfo_encrypted_response_alg VARCHAR2(50),userinfo_encrypted_response_enc VARCHAR2(50),default_max_age NUMBER(10,0) DEFAULT -1,require_auth_time NUMBER(1,0) NOT NULL,default_acr_values VARCHAR2(200),initiate_login_uri VARCHAR2(200),frontchannel_logout_uri VARCHAR2(200),frontchannel_logout_session_required NUMBER(1,0) NOT NULL,backchannel_logout_uri VARCHAR2(200),backchannel_logout_session_required NUMBER(1,0) NOT NULL,backchannel_token_delivery_mode VARCHAR2(4), backchannel_client_notification_endpoint VARCHAR2(1000),backchannel_authentication_request_signing_alg VARCHAR2(10),backchannel_user_code_parameter NUMBER(1,0) NOT NULL,post_logout_redirect_uris VARCHAR2(1000),data NCLOB,PRIMARY KEY (client_id))

CREATE TABLE federation_clients (entity_id VARCHAR2(500) NOT NULL, n INT NOT NULL, anchor_id VARCHAR2(500) NOT NULL, reg VARCHAR2(20) NOT NULL, cid VARCHAR2(100), iat TIMESTAMP(0) NOT NULL, exp TIMESTAMP(0) NOT NULL, misc NCLOB, PRIMARY KEY (entity_id, n))

CREATE TABLE client_jwks (client_id VARCHAR2(100) NOT NULL,jwks CLOB NOT NULL,PRIMARY KEY (client_id))

CREATE TABLE request_jwts (uri VARCHAR2(1000) NOT NULL,client_id VARCHAR2(100) NOT NULL,clm CLOB NOT NULL,frg VARCHAR2(100),iat TIMESTAMP(0) NULL,rol NUMBER(12,0) DEFAULT -1, PRIMARY KEY (uri))

