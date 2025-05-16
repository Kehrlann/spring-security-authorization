# Table of contents

## Basic app local login

- Add request sec
    - Request: hasRole
    - Method Security

## Thinking about Authorization

- Authentication vs Authorization
- XACML
    - Enforcement
    - Decision
    - Information
- What's needed for authorization:
    - Rule
        - Static or dynamic
    - Trying to apply a rule, based on:
        - Operation: a specific action, "getting a resource"
        - Information:
            - Authentication information
            - Context
            - Static or dynamic

## Spring Security mechanics

### Request-level security

- Mention .permitAll(), .denyAll(), .authenticated()
- .hasRole()
    - Mention .hasAuthority() & roles hierarchy ("RBAC")
    - Mention .hasVariable()
- .access()
- Composability

### Method level security

- @PreAuthorize
- @PostAuthorize
    - Don't use for SQL filtering!!!
- Mention @PostFilter
    - Don't use for SQL filtering!!!
- Custom annotations && AnnotationTemplateExpressionDefaults

### Field level security

- Field security
    - Mention you can add templates

## Information is key

- Login-based timing
    - HTTP-basic vs LoginForm
    - OidcUserService
- Context is key
    - the "localhost" example
