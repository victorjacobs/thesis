#!/bin/bash
if [ -z "$1" ]; then
  echo "Usage: $0 <RunName> [<nbThreads>]"
  exit
fi

REMOTE_CMD="./run.sh"

if [ ! -z "$2" ]; then
  REMOTE_CMD=$REMOTE_CMD" -t "$2
fi

HOST="feynman"
RUN="$(date +%s)$1"

echo "Executing $RUN on $HOST"

cd '/Users/victor/Documents/Leuven/Thesis/Code/out/artifacts/'
echo "Build time: "$(stat -f "%Sm" Thesis_jar/Thesis.jar)

if [ ! -d "Thesis_jar/files" ]; then
  echo "Copying scenarios into payload..."
  mkdir Thesis_jar/files
  cp -r '/Users/victor/Documents/Leuven/Thesis/Code/files/scenarios' Thesis_jar/files
fi

echo "Compressing payload..."
tar -zcf payload.tar.gz Thesis_jar
echo "Uploading..."
scp payload.tar.gz feynman.cs.kuleuven.be:~/vault
rm payload.tar.gz
echo "Executing..."
ssh feynman.cs.kuleuven.be "export RUN='$RUN'; cd vault; "$REMOTE_CMD

cd '/Users/victor/Documents/Leuven/Thesis/Results/'
echo "Fetching results..."
scp feynman.cs.kuleuven.be:~/vault/results.$HOST.tar.gz .
tar -xf results.$HOST.tar.gz
rm results.$HOST.tar.gz

echo "Generating images..."
cd $RUN
cd main
rscript '/Users/victor/Documents/Leuven/Thesis/Code/files/diagram2.R'

open .
