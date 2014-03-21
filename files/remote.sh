#!/bin/bash
if [ -z "$1" ]; then
  echo "Usage: $0 <RunName> [<RemoteHost> <TunnelPort>]"
  exit
fi

RUN="$(date +%s)$1"

if [ -z "$2" ]; then
  HOST="pittem"
else
  HOST=$2
fi

if [ -z "$3" ]; then
  TUNNEL_PORT=4005
else
  TUNNEL_PORT=$3
fi

echo "Executing $RUN on $HOST with local port $TUNNEL_PORT"

cd '/Users/victor/Documents/Leuven/Thesis/Code/out/artifacts/'
echo "Build time: "$(stat -f "%Sm" Thesis_jar/Thesis.jar)

if [ ! -d "Thesis_jar/files" ]; then
  echo "Copying scenarios into payload..."
  mkdir Thesis_jar/files
  cp -r '/Users/victor/Documents/Leuven/Thesis/Code/files/scenarios' Thesis_jar/files
fi

echo "Opening tunnel..."
ssh s0212968@st.cs.kuleuven.be -L $TUNNEL_PORT:$HOST.cs.kotnet.kuleuven.be:22 -N &
TUNNEL_PID=$!
sleep 2

echo "Compressing payload..."
tar -zcf payload.tar.gz Thesis_jar
echo "Uploading..."
scp -P $TUNNEL_PORT payload.tar.gz s0212968@localhost:~/vault
rm payload.tar.gz
echo "Executing..."
ssh s0212968@localhost -p $TUNNEL_PORT "export RUN='$RUN'; cd vault; ./run.sh"

cd '/Users/victor/Documents/Leuven/Thesis/Results/'
echo "Fetching results..."
scp -P $TUNNEL_PORT s0212968@localhost:~/vault/results.$HOST.tar.gz .
tar -xf results.$HOST.tar.gz
rm results.$HOST.tar.gz

kill $TUNNEL_PID

echo "Generating images..."
cd $RUN
rscript '/Users/victor/Documents/Leuven/Thesis/Code/files/diagram2.R'

open .
