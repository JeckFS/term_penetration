#!/bin/bash

ant -f ../build.xml
cp ../deploy/Retransmission.jar ../lib
rm -rf ../deploy/*
cp -r ../lib ../deploy
cp -r ../scripts/* ../deploy
tar cvf ../Retransmission.tar ../deploy