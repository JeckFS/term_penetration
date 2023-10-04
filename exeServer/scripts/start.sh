#!/bin/bash

java -Xms512m -Xmx1024m -cp lib/*: -Dfile.encoding=UTF-8 -DretransIp=127.0.0.1 -DretransPort=8827 Main