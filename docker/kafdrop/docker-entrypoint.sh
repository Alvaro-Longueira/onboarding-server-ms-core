#!/bin/bash
cd /
./wait-for-it.sh --timeout=0 kafka:29092 && ./kafdrop.sh