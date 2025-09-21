---
theme: default
class: 'text-center'
highlighter: prism
lineNumbers: true
transition: none
# use UnoCSS
css: unocss
aspectRatio: "16/9"
colorSchema: "light"
canvasWidth: 1024
layout: cover
---

# **Authorization**
# **in Spring Security**

<br>

## Permissions, roles and beyond

<br>
<br>

### Daniel Garnier-Moiroux

Devoxx Belgium, 2025-10-07


---
layout: image-right
image: /daniel-intro.jpg
class: smaller
---

#### Daniel
### Garnier-Moiroux
<br>

Software Engineer @ Broadcom

- <logos-spring-icon /> Spring
- <logos-bluesky /> @garnier.wf
- <logos-firefox /> https://garnier.wf/
- <logos-github-icon /> github.com/Kehrlann/
- <fluent-emoji-flat-envelope-with-arrow /> contact@garnier.wf

---
layout: center
---

<img src="devoxx-spring-security-youtube.png" style="height: 400px;" />

<br>

### Spring Security: Demystified

Devoxx Belgium 2022! On YouTube!

---
layout: center
---

<img src="intro-spring-security.png" style="height: 400px;" />

<br>

### Spring Security: Architecture Principles

Spring I/O 2024! On YouTube!


---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. ⚙️ Authorization internals
1. 🧠 Authorization design

---

# Spring Security Authorization

1. 🍃 **A demo app**
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. ⚙️ Authorization internals
1. 🧠 Authorization design

---

# A demo app

&nbsp;

You've done this before! Some authentication (login), and then some authorization (permissions).

&nbsp;

**Separation of concerns**: avoid security-related code in your domain code

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 **Quick refresher: spring security basics**
1. 🛠️ Spring Security's authz tooling
1. 🧠 Authorization design

---
layout: image
image: security-filter-chain.png
---

---

# Authentication objects

&nbsp;

`Authentication` objects represent logged-in users.

Contains:
- `principal` == identity
- `authorities` == roles, permissions

Stored in the `SecurityContext`.

---
layout: image-right
image: security-context.png
backgroundSize: contain
---

# SecurityContext

- Thread-local
- Not propagated to child threads
- Cleared after requests is processed

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: spring security basics
1. 🛠️ **Spring Security's authz tooling**
1. ⚙️ Authorization internals
1. 🧠 Authorization design

---

# Spring Security authz tooling

&nbsp;

Three levels to apply tooling:

1. Request-level
1. Method-level
1. Object-level

---

# Spring Security authz tooling

&nbsp;

Three levels to apply tooling:

1. **Request-level**
1. Method-level
1. Object-level

---

# Request-level authorizarion

&nbsp;

`http.authorizeHttpRequests(auth -> { /* ... */ });`

&nbsp;

Simple rules:
- `.permitAll()`, `.denyAll()`, `.authenticated()`, ...
- `.hasRole()`, `.hasAuthority()`, ...

---

# Request-level authorizarion

&nbsp;

For more interesting rules:
<br>
`.access((authSupplier, reqContext) -> { /*...*/ });`

Composability:
<br>
`.access(AuthorizationManagers.anyOf(...))`

⚠️ Does not read the body of the request

---

# Spring Security authz tooling

&nbsp;

Three levels to apply tooling:

1. Request-level
1. **Method-level**
1. Object-level

---

# Method-level authorizarion

&nbsp;

`@PreAuthorize(...)`, `@PostAuthorize(...)`, `@PostFilter(...)` with SpEL expressions.

Avoid complex expressions, and use a bean reference:
<br>
`@PreAuthorize("@authzService.authorize(...)")`

These annotations are not repeatable!

---

# Method-level authorizarion

&nbsp;

Consider custom annotations for de-duplication:
<br>
`@AllowedDomains(domains = { "corp.example.com" })`

It requires and `AnnotationTemplateExpressionDefaults` bean.

---

# Method-level authorizarion

&nbsp;

Even with `@PostAuthorize(...)` or `@PostFilter(...)`
<br>DO filter in your database / service, e.g.:
<br>`SELECT ... WHERE owner.id == authenticationId`

&nbsp;

Enforce separation of concerns with `@HandleAuthorizationDenied(handlerClass = ...)`

---

# Spring Security authz tooling

&nbsp;

Three levels to apply tooling:

1. Request-level
1. Method-level
1. **Object-level**

---

# Object-level authorization

&nbsp;

You can apply security to object methods.

&nbsp;

Annotate call site with `@AuthorizeReturnObject` to create a proxy and enforce those annotations.

&nbsp;


---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: spring security basics
1. 🛠️ Spring Security's authz tooling
1. ⚙️ **Authorization internals**
1. 🧠 Authorization design

---
layout: image
image: authz-internals-1.png
---

---
layout: image
image: authz-internals-2.png
---

---
layout: image
image: authz-internals-3.png
---

---
layout: image
image: authz-internals-4.png
---

---
layout: image
image: authz-internals-5.png
---

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: spring security basics
1. 🛠️ Spring Security's authz tooling
1. ⚙️ Authorization internals
1. 🧠 **Authorization design**

---

# Thinking about authorization

<br>

<br>

<br>

### Remember XACML?

---
layout: image
image: xacml-1.png
---

---
layout: image
image: xacml-2.png
---


---

# Spring Security Authorization

1. 🍃 A demo app
1. 🛠️ Spring Security's authz tooling
    1. Request-level
    1. Method-level
    1. Object-level
1. **🧠 Authorization design**
    1. Thinking about authorization
    1. **Information is key**

---

# Information is key

&nbsp;

- What information is relevant for the security decision?
- Where can you capture or transform it?
- Consider:
    - `AuthenticationConverter` works on HttpServletRequest
    - `AuthenticationProvider` works on Authentication
    - `AuthenticationDetailsSource` also HttpServletRequest
    - Last resort: `Filter`

---

# Information is key

&nbsp;

Context matters!

Dedicated `SecurityFilterChain`s with different rules bring the complexity down:
- Simpler information gathering
- Simpler rules

<v-click>

_PSA: Don't create 65 `SecurityFilterChain`s._

</v-click>

---

## References

&nbsp;

#### <logos-github-icon /> https://github.com/Kehrlann/spring-security-authorization

<br>

- <logos-bluesky /> @garnier.wf
- <logos-firefox /> https://garnier.wf/
- <fluent-emoji-flat-envelope-with-arrow /> contact@garnier.wf


---
layout: image
image: /meet-me.jpg
class: end
---

# **Merci 😊**

