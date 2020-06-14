#!/bin/bash

set -e

sbtver=1.3.8
sbtjar=.sbt-launch.jar
sha256=305e7202756bae9887810e3a81070ccd94d966c60e5e3e3c6082455ab33eb59a
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
