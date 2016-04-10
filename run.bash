#!/usr/bin/env bash
PREVIOUS_PATH=`pwd`
SCRIPT=`perl -e 'use Cwd "abs_path";print abs_path(shift)' $0`
SCRIPTPATH=`dirname $SCRIPT`
cd $SCRIPTPATH

spark-submit \
    --master local[8] \
    --class com.github.osblinnikov.reuters.classifier.Main \
    build/libs/reuters-classification.jar \
    ${*:1}

cd $PREVIOUS_PATH