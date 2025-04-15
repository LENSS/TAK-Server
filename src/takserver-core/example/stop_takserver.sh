#!/bin/bash

for NAME in api messaging config; do
  PID_FILE="takserver_${NAME}.pid"
  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if kill "$PID" > /dev/null 2>&1; then
      echo "Stopped TAK Server (${NAME}) [PID: $PID]"
      rm "$PID_FILE"
    else
      echo "Failed to stop TAK Server (${NAME}) [PID: $PID] — it may not be running."
    fi
  else
    echo "PID file for takserver_${NAME} not found."
  fi
done
