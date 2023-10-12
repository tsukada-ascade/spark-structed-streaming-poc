#
# Makefile
#
.PHONY: all
all: jar

BUILDER := sbtscala/scala-sbt:openjdk-11.0.16_1.7.3_2.12.17
UID := $(shell id -u)
GID := $(shell id -g)
USER := -u $(UID):$(GID)
BINDS := -v $(CURDIR):/work -v /etc/passwd:/etc/passwd -v $(HOME):$(HOME)
WORKDIR := -w /work
SBT_OPTS := -mem 2048 --sbt-dir .sbt --sbt-boot .sbt/boot -sbt-cache .sbt/cache
SBT_OPTS += --ivy .sbt/ivy2 -Dsbt.coursier.home=.sbt/cache

jar:
	docker run --rm --net=host $(USER) $(BINDS) $(WORKDIR) $(BUILDER) \
		sbt $(SBT_OPTS) assembly

clean:
	$(RM) -rf .sbt
	$(RM) -rf project/project
	$(RM) -rf project/target
	$(RM) -rf target
