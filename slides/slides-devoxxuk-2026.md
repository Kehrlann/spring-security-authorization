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

Devoxx UK, 2026-05-06


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

<img src="devoxx-uk-spring-security-youtube.png" style="height: 400px;" />

<br>

### Spring Security: The Good Parts™

Devoxx UK 2024! On YouTube!

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⛓️ Beyond the filter chain
1. 🧪 A word on testing

---

# Spring Security Authorization

1. **🍃 A demo app**
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⛓️ Beyond the filter chain
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
1. **🤓 Quick refresher: authentication basics**
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⛓️ Beyond the filter chain
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

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: authentication basics
1. **🛠️ Spring Security's authz tooling**
1. 🧬🔑 Multi-factor authentication
1. ⛓️ Beyond the filter chain
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
1. **🧬🔑 Multi-factor authentication**
1. ⛓️ Beyond the filter chain
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
1. **⛓️ Beyond the filter chain**
1. 🧪 A word on testing

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

# Beyond the filter chain

&nbsp;

Context matters!

Dedicated `SecurityFilterChain`s with different rules bring the complexity down:
- Simpler information gathering
- Simpler rules

<v-click>

_PSA: Don't create 65 `SecurityFilterChain`s._

</v-click>

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🤓 Quick refresher: authentication basics
1. 🛠️ Spring Security's authz tooling
1. 🧬🔑 Multi-factor authentication
1. ⛓️ Beyond the filter chain
1. **🧪 A word on testing**

---

# A word on testing

&nbsp;

Good tooling for authorities-based testing:
- `@WithMockUser(...)`, `@WithUserDetails(...)`
- `SecurityMockMvcRequestPostProcessors.` : `user`, `csrf`, `authentication`, `oidcLogin`, ...

Hardcore: implement your `@WithSecurityContextFactory`

Does not test authentication & information transformation


---

## References

&nbsp;

#### <logos-github-icon /> https://github.com/Kehrlann/spring-security-authorization

<!-- qrencode -s 9 -m 2 -o rating-devoxxuk.png https://m.devoxx.com/events/devoxxuk26/talks/5768/authorization-in-spring-security-permissions-roles-and-beyond -->
<div style="float:right; margin-right: 50px; text-align: center;">
    <a href="https://m.devoxx.com/events/devoxxuk26/talks/5768/authorization-in-spring-security-permissions-roles-and-beyond" target="_blank">
        <img src="/rating-devoxxuk.png" style="margin-bottom: -45px; height: 300px;" >
    </a>
</div>

<br>

- <logos-bluesky /> @garnier.wf
- <logos-firefox /> https://garnier.wf/
- <fluent-emoji-flat-envelope-with-arrow /> contact@garnier.wf

---
layout: center
---

<logos-youtube-icon /> Spring Security intro

<a href="https://www.youtube.com/watch?v=kwxRe-4dnVU" target="_blank">
    <img src="/qr-code-youtube-devoxxuk.png" style="margin-top: 20px; margin-bottom: -45px; height: 300px;" >
</a>

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
    <div>45% off <i>everything!</i></div>
    <br>
    <div><b>devoxxuk26</b></div>
    <br>
    <div>(until May 20th)</div>
</div>
