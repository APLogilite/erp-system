You are implementing Phase P2 of the ERP Identity Platform.

P1 (Identity Domain Model) is complete.

DO NOT modify the database schema unless absolutely required.

--------------------------------------------------
OBJECTIVE
--------------------------------------------------

Implement enterprise-grade authentication.

This is NOT only login.

It must become the permanent authentication engine for the ERP platform.

--------------------------------------------------
TECH STACK
--------------------------------------------------

Spring Boot 3

Java 21

Spring Security 6

JWT

Refresh Tokens

PostgreSQL

JPA

Flyway

--------------------------------------------------
IMPLEMENT
--------------------------------------------------

Authentication Service

JWT Provider

Refresh Token Service

Password Encoder

Authentication Manager

Login API

Logout API

Refresh Token API

Current User API

Change Password API

--------------------------------------------------
JWT REQUIREMENTS
--------------------------------------------------

Access Token

Refresh Token

Expiration

JWT ID (JTI)

Issued Time

Expiration Time

Tenant-safe

Future SSO compatible

--------------------------------------------------
LOGIN FLOW
--------------------------------------------------

User submits

username

password

↓

Validate credentials

↓

Check account active

↓

Check tenant access

↓

Create UserSession

↓

Generate Access Token

↓

Generate Refresh Token

↓

Return AuthenticationResponse

--------------------------------------------------
USER SESSION
--------------------------------------------------

Persist session information.

Store

User

JWT ID

Refresh Token

IP Address

Browser

Device

Login Time

Last Activity

Expiration

Logout Time

Session Status

Support multiple active sessions.

--------------------------------------------------
PASSWORD POLICY
--------------------------------------------------

Support

BCrypt

Password Change

Password Expiry (future)

Password History (placeholder)

Account Locking (placeholder)

--------------------------------------------------
SPRING SECURITY
--------------------------------------------------

Configure

SecurityFilterChain

JWT Filter

AuthenticationEntryPoint

AccessDeniedHandler

Public Endpoints

Protected Endpoints

--------------------------------------------------
PUBLIC ENDPOINTS
--------------------------------------------------

POST /api/auth/login

POST /api/auth/refresh

POST /api/auth/logout

--------------------------------------------------
PROTECTED ENDPOINTS
--------------------------------------------------

GET /api/auth/me

POST /api/auth/change-password

--------------------------------------------------
RESPONSE
--------------------------------------------------

Return

Access Token

Refresh Token

Token Expiration

Basic User Information

Do NOT return permissions yet.

--------------------------------------------------
DO NOT IMPLEMENT
--------------------------------------------------

Roles

Permissions

Menus

Metadata Security

Context Selection

Organization Selection

Company Selection

These belong to later phases.

--------------------------------------------------
TEST CASES
--------------------------------------------------

Valid Login

Invalid Password

Disabled User

Expired Token

Refresh Token

Logout

Concurrent Sessions

--------------------------------------------------
ACCEPTANCE
--------------------------------------------------

✔ Login works

✔ JWT works

✔ Refresh works

✔ Logout works

✔ Session persisted

✔ Spring Security configured

✔ Authentication independent of business modules

✔ Enterprise-grade code

Generate complete implementation.