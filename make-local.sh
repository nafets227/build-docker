#!/bin/bash
#
# Make all docker containers locally
#
# (C) 2017 Stefan Schalelnberg
#

function build_local () {
	printf "Building %s ... \n" "$f"
	docker build -t nafets227/$f $f
	rc=$?; if [ $rc -ne 0 ]; then return $rc ; fi
}

if [ $# -eq 0 ] ; then
	for f in $(ls); do
		if [ -d $f ] ; then
			build_local $f
			rc=$?
			if [ $rc -ne 0 ]; then 
				exit $rc
			fi
		fi
	done
else
	for f in "$@" ; do
		build_local $f
		rc=$?
		if [ $rc -ne 0 ]; then 
			exit $r
		fi
	done
fi

