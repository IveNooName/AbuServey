# AbuServey (Monorepo)

    ServeyFrontend/   Angular (pnpm)       -> wird beim Maven-Build gebaut und als Jar verpackt
    ServeyBackend/    Spring Boot + Postgres -> bindet das Frontend ein und liefert es aus

## Alles bauen (Frontend + Backend in einem Jar)

    ./mvnw clean package
    java -jar ServeyBackend/target/servey-backend-0.0.1-SNAPSHOT.jar

Ablauf: frontend-maven-plugin installiert Node (lokal in ServeyFrontend/node), `pnpm install --frozen-lockfile`,
`pnpm build`, kopiert `dist/ServeyFrontend/browser` nach `META-INF/resources`; das Backend zieht dieses Jar als Dependency.
Nur Backend (ohne Angular-Build): `./mvnw package -Dfrontend.skip=true`

## Entwickeln

    docker compose up -d
    cd ServeyBackend && ../mvnw spring-boot:run -Dspring-boot.run.profiles=dev   # :8080
    cd ServeyFrontend && pnpm start                                              # :4200, /api -> :8080

Swagger: http://localhost:8080/swagger-ui.html  (Admin: "Authorize", dev-Key `dev-admin-key`)

## Konfiguration (Env)

DB_URL, DB_USER, DB_PASSWORD, ADMIN_KEY, COOKIE_SECURE (true bei HTTPS), TOKENS_REQUIRED (true = nur mit Einladungs-Link)
