#!/bin/sh
set -eu

# Wait until JDBC host from SPRING_DATASOURCE_URL is resolvable and accepts TCP.
# Prevents Flyway/EMF crash loops on deploy when postgres DNS is not ready yet.

url="${SPRING_DATASOURCE_URL:-}"
host=""
port="5432"

if [ -n "$url" ]; then
  # jdbc:postgresql://host:port/db  or  jdbc:postgresql://host/db
  rest="${url#jdbc:postgresql://}"
  hostport="${rest%%/*}"
  host="${hostport%%:*}"
  case "$hostport" in
    *:*) port="${hostport##*:}" ;;
  esac
fi

if [ -z "$host" ]; then
  host="postgres"
fi

echo "[entrypoint] waiting for postgres at ${host}:${port} ..."
i=0
max="${WAIT_FOR_DB_SECONDS:-90}"
while [ "$i" -lt "$max" ]; do
  if getent hosts "$host" >/dev/null 2>&1; then
    if nc -z "$host" "$port" >/dev/null 2>&1; then
      echo "[entrypoint] postgres is reachable"
      exec java -jar /app/app.jar "$@"
    fi
  fi
  i=$((i + 1))
  sleep 1
done

echo "[entrypoint] ERROR: postgres not reachable at ${host}:${port} after ${max}s" >&2
echo "[entrypoint] Check: same Docker network, service name, SPRING_DATASOURCE_URL" >&2
exit 1
