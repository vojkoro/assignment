#!/bin/bash
mvn clean test -DUSE_DELAY=false | grep -e 'EventProcessor'