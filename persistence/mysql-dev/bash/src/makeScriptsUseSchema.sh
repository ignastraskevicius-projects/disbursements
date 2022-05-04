#/bin/bash

if [ $# -ne 1 ]; then
	echo 1>&2 "Missing argument: directory containing flyway scripts"
	exit 2;
fi

[ ! -d "$1" ] && echo "Invalid argument: supplied directory does not exist" && exit 2
cd "$1"; 
for f in V*; do 
	echo "USE disbursements;" > use_with_schema;
	cat $f >> use_with_schema; 
	cat use_with_schema > $f; 
	rm use_with_schema; 
done
