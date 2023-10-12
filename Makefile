#
# Makefile
#
.PHONY: all
all: jar

oteljar_url := https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.30.0/opentelemetry-javaagent.jar

jar:
	(mkdir -p extra-jars; cd extra-jars && test -e opentelemetry-javaagent.jar || wget $(oteljar_url))
	(cd producer; make jar)
	(cd spark-consumer; make jar)

clean:
	(cd spark-consumer; make clean)
	(cd producer; make clean)

up:
	docker compose up -d

down:
	docker compose down
	docker volume prune -f
