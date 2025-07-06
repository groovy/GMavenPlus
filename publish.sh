#!/bin/bash

set -o errorexit -o nounset -o pipefail


if [ $# -ne 2 ]; then
  echo "Usage: $0 <username> <password>"
  exit 1
fi

username="$1"
password="$2"
bearer_token=$(printf "%s:%s" "$username" "$password" | base64)

headers=(-H "Authorization: Bearer $bearer_token" -H "Accept: application/json")

response=$(curl -s "${headers[@]}" "https://ossrh-staging-api.central.sonatype.com/manual/search/repositories?profile_id=org.codehaus.gmavenplus&state=open")

repo_count=$(echo "$response" | jq '.repositories | length')
if [ "$repo_count" -ne 1 ]; then
  echo "Error: Expected exactly 1 open repository, but found $repo_count."
  exit 1
fi
repo_key=$(echo "$response" | jq -r '.repositories[0].key')

curl -s -X POST "${headers[@]}" "https://ossrh-staging-api.central.sonatype.com/manual/upload/repository/$repo_key" > /dev/null

echo "Go to https://central.sonatype.com/publishing/deployments and click 'Publish'."
