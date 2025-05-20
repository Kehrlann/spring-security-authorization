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

Spring I/O, 2025-05-22


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

<img src="intro-spring-security.png" style="margin-top: -40px; height: 400px;" />

<br>

### Intro to Spring Security

Spring I/O 2024! On youtube!


---

# Spring Security Authorization

1. 🍃 A demo app
1. 🛠️ Spring Security's authz tooling
    1. Request-level
    1. Method-level
    1. Object-level
1. 🧠 Authorization design
    1. Thinking about authorization
    1. Information is key

---

# Spring Security Authorization

1. **🍃 A demo app**
1. 🛠️ Spring Security's authz tooling
    1. Request-level
    1. Method-level
    1. Object-level
1. 🧠 Authorization design
    1. Thinking about authorization
    1. Information is key

---

# A demo app

&nbsp;

You've done this before! Either request-level or method-level security.

But don't forget **separation of concerns**: avoid security-related in your domain code

---

# Spring Security Authorization

1. 🍃 A demo app
1. **🛠️ Spring Security's authz tooling**
    1. **Request-level**
    1. Method-level
    1. Object-level
1. 🧠 Authorization design
    1. Thinking about authorization
    1. Information is key

---

# Request-level authorizarion

&nbsp;

`http.authorizeHttpRequests(auth -> { /* ... */ });`

&nbsp;

Simple `.permitAll()`, `.authenticated()` and `.hasRole()`.

&nbsp;

For more interesting rules:

`.access((authSupplier, reqContext) -> { /*...*/ });`

---

# Spring Security Authorization

1. 🍃 A demo app
1. **🛠️ Spring Security's authz tooling**
    1. Request-level
    1. **Method-level**
    1. Object-level
1. 🧠 Authorization design
    1. Thinking about authorization
    1. Information is key

---

# Method-level authorizarion

&nbsp;

`@PreAuthorize(...)`, `@PostAuthorize(...)`, with SpEL expressions.

Avoid complex expressions, and use a bean reference in the authorization methods: `@PreAuthorize("@authzService.authorize(...)")`. 

Consider custom annotations.

---

# Method-level authorizarion

&nbsp;

Only use `@PostAuthorize(...)` when you can't filter the results. When possible, filter in your
database / service, e.g.:<br> `SELECT ... WHERE owner.id == authenticationId`

&nbsp;

Enforce separation of concerns with `@HandleAuthorizationDenied(handlerClass = ...)`.

---

# Spring Security Authorization

1. 🍃 A demo app
1. **🛠️ Spring Security's authz tooling**
    1. Request-level
    1. Method-level
    1. **Object-level**
1. 🧠 Authorization design
    1. Thinking about authorization
    1. Information is key

---

# Object-level authorization

&nbsp;

You can apply security to object methods.

Annotate call site with `@AuthorizeReturnObject` to create a proxy and enforce those methods.

---

# Spring Security Authorization

1. 🍃 A demo app
1. 🛠️ Spring Security's authz tooling
    1. Request-level
    1. Method-level
    1. Object-level
1. **🧠 Authorization design**
    1. **Thinking about authorization**
    1. Information is key

---

# Thinking about authorization

Remember XACML?


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

## References

&nbsp;

#### **<logos-github-icon /> https://github.com/Kehrlann/spring-boot-testing**

<!-- qrencode -s 9 -m 2 -o qr-code.png https://mobile.devoxx.com/events/devoxxuk25/rate-talk/2999 -->
<div style="float:right; margin-right: 50px; text-align: center;">
  <img src="/qr-code.png" style="margin-bottom: -45px; height: 300px;" >
</div>

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

