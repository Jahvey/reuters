#!/usr/bin/env bash
PREVIOUS_PATH=`pwd`
SCRIPT=`perl -e 'use Cwd "abs_path";print abs_path(shift)' $0`
SCRIPTPATH=`dirname $SCRIPT`
cd $SCRIPTPATH

spark-submit \
    --master local[8] \
    --class com.reuters.classifier.sub.Main \
    build/libs/classification-1.0-SNAPSHOT-all.jar \
    ${*:1}

cd $PREVIOUS_PATH