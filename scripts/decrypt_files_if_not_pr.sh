#!/usr/bin/env bash

if [[ "$TRAVIS_PULL_REQUEST" == "false" ]]; then
    openssl aes-256-cbc -K $encrypted_f513722454d9_key -iv $encrypted_f513722454d9_iv -in secrets.tar.enc -out secrets.tar -d
    tar xvf secrets.tar
fi
