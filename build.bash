#!/usr/bin/env bash
PREVIOUS_PATH=`pwd`
# Absolute path to this script. /home/user/bin/foo.sh
SCRIPT=`perl -e 'use Cwd "abs_path";print abs_path(shift)' $0`
# Absolute path this script is in. /home/user/bin
SCRIPTPATH=`dirname $SCRIPT`
cd $SCRIPTPATH
gradle shadowJar
cd $PREVIOUS_PATH