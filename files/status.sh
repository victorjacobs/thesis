#!/bin/bash
if [ -z "$1" ]; then
  echo "Usage: $0 list"
  exit
fi

# Doesn't really matter what computer to connect to
ssh s0212968@st.cs.kuleuven.be -L 9999:bergen.cs.kotnet.kuleuven.be:22 -N &
TUNNEL_PID=$!
sleep 2

ssh s0212968@localhost -p 9999 "cd vault; ./status.sh"
kill $TUNNEL_PID
