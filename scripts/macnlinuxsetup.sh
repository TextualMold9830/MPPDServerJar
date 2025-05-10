if type -p java; then
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    _java="$JAVA_HOME/bin/java"
else
    #java not found check for sdkman and install it
    if ! command -v sdk >/dev/null 2>&1; then
      curl -s "https://get.sdkman.io" | bash
    fi
    sdk install java 21.0.2-graalce
    _java = java
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" -ge "21" ]]; then
      #java in installed, the rest of this is compatible with MacOS and Linux
      curl https://raw.githubusercontent.com/TextualMold9830/MPPDServerJar/refs/heads/main/scripts/termuxsetup.sh | bash
    else
      echo "Java version is too low, please update to java 21 or newer"
    fi
fi