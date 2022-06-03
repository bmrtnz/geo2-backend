#!/usr/bin/env bash

OUT=sql/output.sql
[[ -f $OUT ]] && rm $OUT
#cat sql/*.sql > $OUT
for f in sql/*.sql; do
  cat $f >> $OUT
  echo '' >> $OUT
done
