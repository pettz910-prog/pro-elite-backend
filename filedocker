# ── Runtime stage ─────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S proelite && adduser -S proelite -G proelite

COPY --from=build /app/target/*.jar app.jar

# Switch to non-root AFTER copying the jar
RUN chown proelite:proelite app.jar
USER proelite

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]