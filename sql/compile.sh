#!/usr/bin/env bash

OUT=sql/output.sql
[[ -f $OUT ]] && rm $OUT
cat sql/*.sql > $OUT
