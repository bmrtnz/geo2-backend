#!/usr/bin/env bash

# compile SQL modified since specified revision
# exemple usage:
# ./sql/compile.sh tags/1.18.2

OUT=sql/output.sql

# give path of SQL files that have been modified since specified revision
get_diff_from() {
    git diff $1 HEAD --name-only | grep **/*.sql
}

# delete output file if it exists
[[ -f $OUT ]] && rm $OUT
echo cleared output file

# merge scripts and write the content in the output file
for f in $(get_diff_from $1); do
    cat $f >> $OUT
    echo '' >> $OUT
    echo added $f
done

echo done writing $OUT
