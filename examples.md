Demo state:

Pages:

- Public page
- Private page

Users:

- daniel, daniel@example.com, ["user"]
- josh, josh@example.com, ["admin", "user"]
- alice, alice@corp.example.com, ["user"]
- bob, bob@ext.example.com, ["user"]

1. Basic app local login

- /admin page -> `.hasRole("...")`
- Admin method on the public page (?mode=admin) -> `@PreAuthorize("hasRole('admin')")`

2. Request-level security

We've seen `.hasRole()`

- /profile/{username} page -> `.hasVariable("...")`
- /corp page -> `.access(email.endsWith("@corp.example.com")`
- /profile/{username} page -> add `.hasRole("admin")`, and then compose

3. Method-level

We've seen `@PreAuthorize`

- `/shipments` page shows shipments ->
  `@PreAuthorize(authentication.email.endsWith('@corp.example.com') or authentication.email.endsWith('@example.com')) `
- `/method-security/profile/{username}` -> `@PostAuthorize(authentication.email.sameDomain(returnObject))`
- `/shipments` page -> `@HasDomain(...)` custom annotation

4. Field-level

- `/shipmemts` address -> `@PreAuthorize("hasRole('admin')")`

6. Information is key

- Login-based timing
    - HTTP-basic vs LoginForm -> custom `AuthenticationDetailsSource`
    - Optional: Custom authentication provider to compare email
- Context is key
    - `/localhost` endpoint allowed only on localhost

Parked:

- Role hierarchy
- External auth service
- Have a fragment to showcase user information
- RC1
- Path variable make it smoother