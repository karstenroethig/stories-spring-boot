#!/bin/sh

DIRNAME=`dirname "$0"`

# Setup APP_HOME
APP_HOME=`cd "$DIRNAME/." >/dev/null; pwd`

# Read an optional running configuration file
if [ "x$RUN_CONF" = "x" ]; then
    RUN_CONF="$DIRNAME/run.conf"
fi
if [ -r "$RUN_CONF" ]; then
    . "$RUN_CONF"
fi

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

# Display our environment
echo "========================================================================="
echo ""
echo "  ${project.name} Bootstrap Environment"
echo ""
echo "  Home Directory: $APP_HOME"
echo ""
echo "  JAVA: $JAVA"
echo ""
echo "  JAVA_OPTS: $JAVA_OPTS"
echo ""
echo "========================================================================="
echo ""

eval \"$JAVA\" $JAVA_OPTS \
	-jar \""$APP_HOME"/${project.artifactId}.jar\" \
