#!/bin/bash
set -e # exit with nonzero exit code if anything fails

openssl aes-256-cbc -K $encrypted_7feff29b77b0_key -iv $encrypted_7feff29b77b0_iv -in deployment.key.enc -out deployment.key -d
secreteFilePath=$(pwd)/deployment.key
echo "Deployment key found"

echo "Executing gradle deployToMavenCentral"
./gradlew deployToMavenCentral -PdeploymentVersion=${TRAVIS_TAG} -Psigning.keyId=${signingKeyId} -Psigning.password=${signingPassword}  -Psigning.secretKeyRingFile=$secreteFilePath -PossrhUsername=${ossrhUsername} -PossrhPassword=${ossrhPassword} --stacktrace --info

rm $secreteFilePath
