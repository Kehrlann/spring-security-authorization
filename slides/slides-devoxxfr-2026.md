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

Devoxx France, 2026-04-23


---
layout: image-right
image: /daniel-broken-shoulder.jpg
class: smaller
---

#### Daniel
### Garnier-Moiroux
<br>

Software Engineer

- <logos-spring-icon /> Spring
- <logos-bluesky /> @garnier.wf
- <logos-firefox /> https://garnier.wf/
- <logos-github-icon /> github.com/Kehrlann/
- <fluent-emoji-flat-envelope-with-arrow /> contact@garnier.wf

---
layout: center
---

<img src="devoxx-france-spring-security-youtube.png" style="height: 400px;" />

<br>

### Spring Security: Décodage et démystification

Devoxx France 2022! On YouTube!

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
1. 🧬🔑 Multi-factor authentication
1. ⚙️ Authorization internals
1. 🧠 Authorization design
1. 🧪 A word on testing

---

# Spring Security Authorization

1. 🍃 **A demo app**
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⚙️ Authorization internals
1. 🧠 Authorization design
1. 🧪 A word on testing

---

# A demo app

&nbsp;

You've done this before! Some authentication (login), and then some authorization (permissions).

&nbsp;

**Separation of concerns**: avoid security-related code in your domain code

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 **Quick refresher: authentication basics**
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. 🧠 Authorization design
1. 🧪 A word on testing

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
1. 🤓 Quick refresher: authentication basics
1. 🛠️ **Spring Security's authz tooling**
1. 🧬🔑 Multi-factor authentication
1. ⚙️ Authorization internals
1. 🧠 Authorization design
1. 🧪 A word on testing

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


```java
// More interesting rules:
.access((authSuppl, reqCtx) -> {
    // ...
    return new AuthorizationDecision(true);
});

// Composability:
.access(AuthorizationManagers.anyOf(...));
```

<br>
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
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 **Multi-factor authentication**
1. ⚙️ Authorization internals
1. 🧠 Authorization design
1. 🧪 A word on testing

---

# Multi-Factor Authentication

&nbsp;

Built around:

- `FactorGrantedAuthority` - special authority
- `@EnableMultiFactorAuthentication`
- `AuthorizationManagerFactory` to adapt `.hasRole(...)` etc

---

# Multi-Factor Authentication

&nbsp;

Use with:

- App-wide with `@EnableMFA(authorities = ...)`
- Endpoint-by-endpoint with `AuthorizationManager<...>`
    - `AllRequiredFactorsAuthorizationManager`

---

# Time-based MFA

```java
// AuthorizationManager
var passwordLastMinute = AllRequiredFactorsAuthorizationManager
	.builder()
	.requireFactor((factor) ->
			factor.passwordAuthority()
				.validDuration(Duration.ofSeconds(30))
	)
	.build();

// usage:
return http
		.authorizeHttpRequests(authz -> {
					authz.requestMatchers("/password")
							.access(passwordLastMinute);
					// ...
				})
		// ...

```

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⚙️ **Authorization internals**
1. 🧠 Authorization design
1. 🧪 A word on testing

---
layout: image
image: security-filter-chain.png
---

---

# Spring Security Filter

```java
public void doFilter(
  HttpServletRequest request,
  HttpServletResponse response,
  FilterChain chain
  ) {
    // 1. Before the request proceeds further
    // (e.g. authentication or reject req)
    // ...

    // 2. Invoke the "rest" of the chain
    chain.doFilter(request, response);

    // 3. Once the request has been fully processed (e.g. cleanup)
    // ...
}
```

---
layout: center
---

<img src="filter-chain-oop.png" style="max-height: 500px;"/>

---
layout: center
---

<img src="filter-chain-call-stack.png" style="max-height: 500px;"/>


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
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⚙️ Authorization internals
1. 🧠 **Authorization design**
1. 🧪 A word on testing

---

# 🧠 Authorization design

<br>

1. **Thinking about authorization**
1. Information is key
1. Multiple filter chains
1. Access control models

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

# 🧠 Authorization design

<br>

1. Thinking about authorization
1. **Information is key**
1. Multiple filter chains
1. Access control models

---

# Information is key

&nbsp;

- What information is relevant for the security decision?
- Where can you capture or transform it?

Make that information available, interfaces are neat.

---

# Information is key

&nbsp;

To extract information at login time, consider:

- `AuthenticationConverter` works on HttpServletRequest
- `AuthenticationDetailsSource` also HttpServletRequest
- `AuthenticationProvider` works on Authentication
- Auth-specific abstractions (eg `OidcUserService`)
- Last resort: `Filter`

---

# 🧠 Authorization design

<br>

1. Thinking about authorization
1. Information is key
1. **Multiple filter chains**
1. Access control models

---

# Multiple filter chains

&nbsp;

Context matters!

Dedicated `SecurityFilterChain`s with different rules bring the complexity down:
- Simpler information gathering
- Simpler rules

<v-click>

_PSA: Don't create 65 `SecurityFilterChain`s._

</v-click>

---

# 🧠 Authorization design

<br>

1. Thinking about authorization
1. Information is key
1. Multiple filter chains
1. **Access control models**

---

# Access control models

<br>

- **RBAC**: Role-Based Access Control
- **ABAC**: Attribute-Based Access Control
- **ReBAC**: RElationship-Based Access Control

(Not mentionig [ACLs](https://github.com/spring-projects/spring-security/tree/main/acl))

---

# Access control models

- **RBAC**: Native to Spring Security, simple
    - But! "Roles explosion"
- **ABAC**: Fine-grained authorization
    - Today, implemented outside of the app (Open Policy Agent)
- **ReBAC**: Great for hierarchies of authz
    - Cool kid on the block, inspired by Google™
    - Always as an external system
    - Many tools: SpiceDB, OpenFGA, Permify, ...

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⚙️ Authorization internals
1. 🧠 Authorization design
1. 🧪 **A word on testing**

---

# A word on testing

&nbsp;

Good tooling for authorities-based testing (`@WithMockUser(...)`)

Hardcore: implement your `@WithSecurityContextFactory`

Does not test authentication & information transformation


---

## References

&nbsp;

#### <logos-github-icon /> https://github.com/Kehrlann/spring-security-authorization

<!-- qrencode -s 9 -m 2 -o qr-code.png https://m.devoxx.com/events/devoxxfr2026/talks/5554/autorisation-avec-spring-security-permissions-roles-et-plus-encore -->
<div style="float:right; margin-right: 50px; text-align: center;">
    <a href="https://m.devoxx.com/events/devoxxfr2026/talks/5554/autorisation-avec-spring-security-permissions-roles-et-plus-encore" target="_blank">
        <img src="/rating-devoxxfr.png" style="margin-bottom: -45px; height: 300px;" >
    </a>
</div>

<br>

- <logos-bluesky /> @garnier.wf
- <logos-firefox /> https://garnier.wf/
- <fluent-emoji-flat-envelope-with-arrow /> contact@garnier.wf

---
layout: two-cols
---

<logos-youtube-icon /> Spring Security intro

<a href="https://www.youtube.com/watch?v=HyoLl3VcRFY" target="_blank">
    <img src="/qr-code-youtube.png" style="margin-top: 20px; margin-bottom: -45px; height: 300px;" >
</a>

::right::

<div style="margin-left: 70px; text-align: center;">
    <a href="https://go.bsky.app/TLqBjSF" target="_blank">
        <img src="/qr-code-bsky-starter.png" style="height: 400px;" >
    </a>
</div>

---
layout: two-cols
---

<div style="height: 100%; display: flex; flex: row; justify-content: center; align-items: center;" >
    <img src="/testing-spring-boot-applications-cover.png" style="" >
</div>

::right::

<div style="height: 100%; display: flex; flex-flow: column; justify-content: center; align-items: center;" >
    <img src="/manning-affiliate-qr-code.png" style="display: block;" >
    <div>https://hubs.la/Q04bFz560</div>
    <br>
    <div>45% de remise!</div>
    <br>
    <div><b>devoxxfr26</b></div>
    <br>
    <div>(jusqu'au 9 mai)</div>
</div>

---
layout: image
image: dimitris-meta.jpg
---
