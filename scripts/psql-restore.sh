#!/bin/bash
docker run -it -v $(pwd)/out:/dump -w /dump --network=doc-man_default -e PGPASSWORD=postgrespassword postgres:12 psql -f /dump/db.dump -U postgres -h postgres