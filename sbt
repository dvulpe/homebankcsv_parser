#!/bin/bash

set -e

sbtver=1.3.10
sbtjar=".sbt/.sbt-launch-${sbtver}.jar"
sha256=b8bb78b91b39fb0036cf97e736333b2e73a2cb098fdcad114851b4aa6cb9a26a
sbtrepo=https://repo1.maven.org/maven2/org/scala-sbt/sbt-launch

validjar() {
  checksum=`openssl dgst -sha256 $sbtjar | awk '{ print $2 }'`
  [ "$checksum" = $sha256 ]
}

if [ -f $sbtjar ] && ! validjar ; then
    echo "bad $sbtjar" >&2
    mv $sbtjar "${sbtjar}.invalid"
fi

if [ ! -f $sbtjar ]; then
  echo "downloading $sbtjar" >&2
  if [ ! -d ".sbt" ]; then mkdir -p .sbt; fi
  curl -L --silent --fail -o $sbtjar $sbtrepo/$sbtver/sbt-launch.jar
fi

if ! validjar ; then
    echo "bad $sbtjar.  delete $sbtjar and run $0 again." >&2
    exit 1
fi

[ -f ~/.sbtconfig ] && . ~/.sbtconfig

java -ea                          \
  $SBT_OPTS                       \
  $JAVA_OPTS                      \
  -XX:ReservedCodeCacheSize=128m  \
  -XX:SurvivorRatio=128           \
  -XX:MaxTenuringThreshold=0      \
  -Xss8M                          \
  -Xms512M                        \
  -Xmx2G                          \
  -server                         \
  -jar $sbtjar "$@"
