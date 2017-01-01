#!/bin/bash
#
# Make all docker containers locally
#
# (C) 2017 Stefan Schalelnberg
#

for f in $(ls); do
	test -d $f && 
	docker build -t nafets227/$f $f
	if [ $? -ne 0 ]; then 
		exit $?
	fi
done
