CREATE TABLE IF NOT EXISTS jwks (ctx TEXT NOT NULL, ts TIMESTAMP NOT NULL, jwks JSON NOT NULL, CONSTRAINT pk_jwks PRIMARY KEY (ctx))

CREATE TABLE IF NOT EXISTS jwks_history (ts TIMESTAMP NOT NULL, ctx TEXT NOT NULL, jwks JSON NOT NULL, CONSTRAINT pk_jwks_history PRIMARY KEY (ts, ctx))

CREATE TABLE IF NOT EXISTS subject_sessions (id TEXT NOT NULL, sub TEXT NOT NULL, auth_ts TIMESTAMP NOT NULL, acr TEXT, amr TEXT[], creation_ts TIMESTAMP NOT NULL, access_ts TIMESTAMP NOT NULL, ctx TEXT, jkt BYTEA, fpt BYTEA, max_life BIGINT DEFAULT -1, auth_life BIGINT DEFAULT -1, max_idle BIGINT DEFAULT -1, claims JSON, rps TEXT[], data JSON, CONSTRAINT pk_subject_sessions PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS subject_index (sub TEXT NOT NULL, n INT NOT NULL, id TEXT NOT NULL, CONSTRAINT pk_subject_index PRIMARY KEY (sub, n))

CREATE TABLE IF NOT EXISTS pending_codes (code TEXT NOT NULL, authz JSON NOT NULL, acl BIGINT DEFAULT -1, CONSTRAINT pk_pending_codes PRIMARY KEY (code))

CREATE TABLE IF NOT EXISTS id_access_tokens (id TEXT NOT NULL, sub TEXT NOT NULL, act TEXT, cid TEXT NOT NULL, scp TEXT[], atl BIGINT DEFAULT -1, ats VARCHAR(10) NOT NULL DEFAULT 'PUBLIC', iss TEXT, iat TIMESTAMP NULL, aud TEXT[], clm TEXT[], cll TEXT[], cld JSON, uip JSON, sik TEXT, dat JSON, oth JSON, cnf JSON, CONSTRAINT pk_id_access_tokens PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS long_lived_authorizations (sub TEXT NOT NULL, act TEXT NOT NULL, cid TEXT NOT NULL, scp TEXT[], scs TEXT[], irt BOOLEAN NOT NULL, rtl BIGINT DEFAULT 0, rtm BIGINT DEFAULT 0, rtr BOOLEAN, idr BOOLEAN, rts TEXT, rti TIMESTAMP NULL, atl BIGINT DEFAULT 0, ate VARCHAR(20) NOT NULL, atc BOOLEAN NOT NULL, ats VARCHAR(10) NOT NULL DEFAULT 'PUBLIC', iss TEXT, iat TIMESTAMP NULL, uat TIMESTAMP NULL, aud TEXT[], clm TEXT[], cll TEXT[], cls TEXT[], cld JSON, uip JSON, dat JSON, CONSTRAINT pk_long_lived_authorizations PRIMARY KEY (sub, act, cid))

CREATE TABLE IF NOT EXISTS revocation_journal (sub VARCHAR(250) NOT NULL, act VARCHAR(250) NOT NULL, cid VARCHAR(100) NOT NULL, ts TIMESTAMP NOT NULL, CONSTRAINT pk_revocation_journal PRIMARY KEY (sub, act, cid))

CREATE TABLE IF NOT EXISTS expended_tokens (t_key TEXT NOT NULL,type TEXT NOT NULL,ts TIMESTAMP NOT NULL,tsp TIMESTAMP,dat TEXT,etl BIGINT, CONSTRAINT pk_expended_tokens PRIMARY KEY (t_key))

CREATE TABLE IF NOT EXISTS auth_sessions (sid TEXT NOT NULL,session TEXT NOT NULL,iat TIMESTAMP NULL,asl BIGINT DEFAULT -1, CONSTRAINT pk_auth_sessions PRIMARY KEY (sid))

CREATE TABLE IF NOT EXISTS consent_sessions (sid TEXT NOT NULL,session TEXT NOT NULL,iat TIMESTAMP NULL,asl BIGINT DEFAULT -1, CONSTRAINT pk_consent_sessions PRIMARY KEY (sid))

CREATE TABLE IF NOT EXISTS clients (client_id TEXT NOT NULL,client_id_issued_at TIMESTAMP NULL,registration_access_token TEXT,client_secret TEXT,client_secret_expires_at TIMESTAMP NULL,grant_types TEXT[],response_types TEXT[],redirect_uris TEXT[],scope TEXT[],token_endpoint_auth_method TEXT,token_endpoint_auth_signing_alg TEXT,jwks_uri TEXT,jwks TEXT,contacts TEXT[],client_name JSON,client_uri JSON,logo_uri JSON,policy_uri JSON,tos_uri JSON,software_id TEXT,software_version TEXT,application_type TEXT,subject_type TEXT,sector_identifier_uri TEXT,request_uris TEXT[],request_object_signing_alg TEXT,request_object_encryption_alg TEXT,request_object_encryption_enc TEXT,authorization_signed_response_alg TEXT,authorization_encrypted_response_alg TEXT,authorization_encrypted_response_enc TEXT,require_pushed_authorization_requests BOOLEAN NOT NULL,tls_client_auth_subject_dn TEXT,tls_client_auth_san_dns TEXT,tls_client_auth_san_uri TEXT,tls_client_auth_san_ip TEXT,tls_client_auth_san_email TEXT,tls_client_certificate_bound_access_tokens BOOLEAN NOT NULL,dpop_bound_access_tokens BOOLEAN NOT NULL,code_challenge_method TEXT,id_token_signed_response_alg TEXT,id_token_encrypted_response_alg TEXT,id_token_encrypted_response_enc TEXT,userinfo_signed_response_alg TEXT,userinfo_encrypted_response_alg TEXT,userinfo_encrypted_response_enc TEXT,default_max_age BIGINT DEFAULT -1,require_auth_time BOOLEAN NOT NULL,default_acr_values TEXT[],initiate_login_uri TEXT,frontchannel_logout_uri TEXT,frontchannel_logout_session_required BOOLEAN NOT NULL,backchannel_logout_uri TEXT,backchannel_logout_session_required BOOLEAN NOT NULL,backchannel_token_delivery_mode TEXT, backchannel_client_notification_endpoint TEXT,backchannel_authentication_request_signing_alg TEXT,backchannel_user_code_parameter BOOLEAN NOT NULL,post_logout_redirect_uris TEXT[],data JSON,CONSTRAINT pk_clients PRIMARY KEY (client_id))

CREATE TABLE IF NOT EXISTS federation_clients (entity_id TEXT NOT NULL, n INT NOT NULL, anchor_id TEXT NOT NULL, reg TEXT NOT NULL, cid TEXT, iat TIMESTAMP NOT NULL, exp TIMESTAMP NOT NULL, misc JSON, CONSTRAINT pk_federation_clients PRIMARY KEY (entity_id, n))

CREATE TABLE IF NOT EXISTS client_jwks (client_id TEXT NOT NULL,jwks TEXT NOT NULL,CONSTRAINT pk_client_jwks PRIMARY KEY (client_id))

CREATE TABLE IF NOT EXISTS request_jwts (uri TEXT NOT NULL,client_id TEXT NOT NULL,clm TEXT NOT NULL,frg TEXT,iat TIMESTAMP NULL,rol BIGINT DEFAULT -1, CONSTRAINT pk_request_jwts PRIMARY KEY (uri))

