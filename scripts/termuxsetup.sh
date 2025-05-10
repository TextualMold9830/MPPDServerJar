! command -v java 2>&1 >/dev/null && pkg install openjdk-21 -y
test -f start.sh && (sh start.sh; exit 0)
(test -d MPPD || mkdir MPPD/); cd MPPD
curl -LO "https://github.com/TextualMold9830/MPPDServerJar/releases/download/Stable/MPPDServer.jar" && echo "Server successfully downloaded"
echo "java -jar MPPDServer.jar"  > start.sh
chmod +x start.sh
cd ..
echo "cd MPPD/; java -jar MPPDServer.jar"  > start.sh
chmod +x start.sh
echo "From now on execute ./start.sh to start the server"
./start.sh