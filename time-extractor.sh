#bin/bash

cat   $1 | jq '[leaf_paths as $path | {"key": $path | join("."), "value": getpath($path)}]  | from_entries' |  jq 'with_entries(if (.key|test(".(start|end|client)Time.*")) then ( {key: .key, value: .value } ) else empty end )' > $2