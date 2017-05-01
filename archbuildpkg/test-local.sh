#!/bin/bash
# Test archbuildpkg on local docker.
#
# (C) 2017 Stefan Schallenberg

function TEST_RESULT {
	if [ "$#" -lt 2 ] ; then
		printf "Internal Error: test_result called with %s instead of 2 parms. Aborting\n" \
			"$#" 
		exit 1
	fi
	
	if [ "$2" -eq 0 ]; then
		printf "Test %s succeeded.\n" "$1"
		TESTS_SUCC="$TESTS_SUCC $1"
	else
		if [ -r "$3" ] ; then
			printf "Test %s failed.\n" "$1"
		else
			printf "Test %s failed (%s).\n" "$1" "$3"
		fi
		TESTS_FAIL="$TESTS_FAIL $1"
		fi
}

function TEST_SUMMARY {
	printf "Test Summary: %s executed, %s OK , %s FAIL\n" \
		"$(wc -w <<<"$TESTS_SUCC $TESTS_FAIL")" \
		"$(wc -w <<<"$TESTS_SUCC")" \
		"$(wc -w <<<"$TESTS_FAIL")"
	if [ -z $TESTS_FAIL ] ; then
		return 0
	else
		printf "\tFailed Tests: %s\n" "$TESTS_FAIL"
		return 2
	fi
	
}

function run_container {
	if [ "$cfg_container_trace" -eq 0 ] ; then
		docker run \
			--volume=$CONT_CONF:/config:ro \
			"$@" \
			$IMAGE /bin/true
	else
		docker run \
			--entrypoint /bin/bash \
			--volume=$CONT_CONF:/config:ro \
			"$@" \
			$IMAGE \
			-x /usr/local/bin/archbuildpkg /bin/true
	fi
}

readonly IMAGE="nafets227/archbuildpkg:testlocal"
readonly CONT_CONF="$(realpath "$(dirname "$BASH_SOURCE")")/testconfig"

mkdir -p $CONT_CONF >/dev/null

if [[ $* =~ "--container-out" ]] ; then
	cfg_container_out=1
else
	cfg_container_out=0
fi

if [[ $* =~ "--container-trace" ]] ; then
	cfg_container_trace=1
	cfg_container_out=1
else
	cfg_container_trace=0
fi

if [[ $- = *x* ]] ; then
	# This script is running in trace mode, so we enable all other traces
	cfg_container_trace=1
	cfg_container_out=1
fi


# Create docker image ########################################################
docker build -t $IMAGE .
rc=$? ; if [ $rc -ne 0 ] ; then
	printf "docker build RC=%s. Aborting.\n" "$rc"
	return 1
fi

TEST="NoArg" #################################################################
# Test simple start with no arguments
run_container 
rc=$? ; if [ $rc -ne 0 ]; then
	printf "Test %s failed. RC=%s\n" "$TEST" "$rc"
fi
[ $rc -eq 0 ]
result=$?
TEST_RESULT "$TEST" "$result" "RC=$rc"  

TEST="RepoNafets" ############################################################
cp -a /etc/ca-certificates/trust-source/anchors/nafetsde-ca.crt \
	$CONT_CONF/nafetsde-ca.crt
run_container \
	-e PACMAN_REPO=nafets \
	-e 'PACMAN_REPO_NAFETS=Server = http://www.intranet.nafets.de/archlinux/$repo/os/$arch' \
	-e SSL_CAFILE="/config/nafetsde-ca.crt"
rc=$?
[ $rc -eq 0 ]
result=$?
TEST_RESULT "$TEST" "$result" "RC=$rc"  

TEST="Todo" ##################################################################
#TODO Test mit PACMAN_MIRRORLIST
#TODO Test mit PACMAN_KEY

##### Ende of Tests ##########################################################
TEST_SUMMARY
# return code of TEST_SUMMARY is also our return code!