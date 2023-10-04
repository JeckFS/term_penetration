#!/bin/bash

java -Xms512m -Xmx1024m -cp lib/*: -Dfile.encoding=UTF-8 -DretransPort=8827 Main