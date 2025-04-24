# Roles, authorization, permissions

Goals:
- Overview of all authz in Spring Sec
- "Best practices": in authz (bring nuance)
- Authz @ Different points of the request lifecycle

Anti-goals:
- Specific integrations (e.g. OpenFGA)

## Thoughts:

What's the story?
-> Mechanics of authz (req security, method sec, authn transformation, auth manager)
-> Philosophy of authz (when? RBAC / ReBAC?)


## Table of contents

### Basic app local login

- Add request sec
    - SpEL: hasRole || hasRole ... attribute ? (email) ... @bean.method
    - .access

### Method security

- @PrePost authorize -> access denied exc
- @PrePost filter
- Custom annotations

### Field level security

### Tentative: use-cases -> mTLS vs Username/Password

### Rebac?

## Brain dump

Showcase:
- Req sec roles / authorities
    - SpEL
    - AuthManager .access
- Method Sec
    - @PreAuth
    - @PostAuth
    - @Pre/Post Filter
    - Custom annotations
    - Custom auth manager
- Field security
- RBAC
    - Roles
    - Roles hierarchy
- ReBAC with openFGA?
- Authz prep @ login time
- Different login methods == different routes
- Different emails == different routes
- Testing?

## Abstract

https://2025.springio.net/sessions/authorization-in-spring-security-permissions-roles-and-beyond/

**Authorization in Spring Security: permissions, roles and beyond**

When creating Spring Boot apps, Spring Security is the go-to choice for all your security use-cases. It offers protections against exploits, authentication (who is the user?) and authorization (are they allowed to do X?) capabilities. Basic authorization features, such as hasRole(...), are easy to implement, but things quickly become complicated when you have more advanced use-cases.

Many operations must be architected correctly to provide secure and robust authorization, in multiple phases. During the initial login phase, the relevant information about the user is extracted, transformed and stored, for example user data from OpenID claims. Then, for authorization, “policy decision” and “policy enforcement” are defined within the context of an operation: where are the authorization decisions made? Lastly, strategies are implemented in code to produce those authorization decisions.

This talk is the follow-up of Spring Security Architecture Principles talk at Spring I/O 2024 (https://www.youtube.com/watch?v=HyoLl3VcRFY). Through live-coded examples, you will build a solid, foundational understanding for all your authorization architecture. You will get an overview of all the access control patterns you can apply with Spring Security. And you will get practical advice on different authorization mechanisms available, and their tradeoffs.
