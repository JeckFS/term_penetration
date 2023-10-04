#!/bin/bash

ant -f ../build.xml
cp ../deploy/ExeServer.jar ../lib
rm -rf ../deploy/*
cp -r ../lib ../deploy
cp -r ../scripts/* ../deploy
tar cvf ../ExeServer.tar ../deploy