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