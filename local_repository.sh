jarName=$(mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout)
projectVersion=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

folderPath="C:/Users/giola/Documents/Personal Project/Local Repository/io/github/giovannilamarmora/utils/utils-code/${projectVersion}"

mkdir -p "${folderPath}"

cp "target/${jarName}.jar" "${folderPath}"

cd "${folderPath}"

ls

sha256sum "${jarName}.jar" > "${jarName}.jar.sha256"