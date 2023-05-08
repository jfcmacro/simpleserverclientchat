#!/usr/bin/bash

info () {
    echo "execClient.sh [-l] [-h host] [-p port] [-u username]"
    echo "execClient.sh -i"
    echo "execClient.sh -v"
    exit 0
}

version () {
    echo "No version number already assigned"
    exit 0
}

optstring="lh:ip:u:v"
LIB=$HOME/lib
OPTS=""

while getopts $optstring arg; do
    case ${arg} in
        l)
            OPTS="$OPTS -l"
            ;;
        h)
            OPTS="$OPTS -h ${OPTARG}"
            ;;
        i)
            info
            ;;
        p)
            OPTS="$OPTS -p ${OPTARG}"
            ;;
        u)
            OPTS="$OPTS -u ${OPTARG}"
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

java -cp $LIB/commons-cli-1.5.0.jar:$LIB/lanterna-3.1.1.jar:target/classes com.epam.rd.chat.client.Client ${OPTS}
