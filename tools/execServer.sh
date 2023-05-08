#!/usr/bin/bash

info() {
    echo "execServer.sh [-e] [-l] [-p port]"
    echo "execServer.sh -i"
    echo "execServer.sh -v"
    exit 0
}

version() {
    echo "No version number already assigned"
    exit 0
}

optstring="eilp:v"
LIB=$HOME/lib
OPTS=""

while getopts $optstring arg; do
    case ${arg} in
        e)
            OPTS="$OPTS -e"
            ;;
        i)
            info
            ;;
        l)
            OPTS="$OPTS -l"
            ;;
        p)
            OPTS="$OPTS -p ${OPTARG}"
            ;;
        v)
            version
            ;;
        *)
            echo "Invalid options -${OPTARG}"
            exit 2
            ;;
    esac
done

java -cp $LIB/commons-cli-1.5.0.jar:target/classes com.epam.rd.chat.server.ChatServer ${OPTS}
