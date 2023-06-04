.PHONY: docker-load
docker-load:
	docker load < out/doc-man-render.tar.gz
	docker load < out/docman-ui.tar.gz
	docker load < out/graphql-engine.tar.gz
	docker load < out/postgres.tar.gz

.PHONY: docker-save
docker-save:
  docker save doc-man-render:latest | gzip > out/doc-man-render.tar.gz
	docker save 481436215387.dkr.ecr.ap-east-1.amazonaws.com/docman-ui:9544e46de5cf66b1ecdc167da5cd2de951ec1a9d | gzip > out/docman-ui.tar.gz
	docker save hasura/graphql-engine:v2.15.2 | gzip > out/graphql-engine.tar.gz
	docker save postgres:12 | gzip > out/postgres.tar.gz

.PHONY: psql-dump
psql-dump:
	docker run -it --network=doc-man_default -e PGPASSWORD=postgrespassword postgres:12 pg_dump -h postgres -U postgres

.PHONY: psql-restore
psql-restore:
  docker run -it -v $(pwd):/dump -w /dump --network=doc-man_default -e PGPASSWORD=postgrespassword postgres:12 psql -f /dump/db.dump -U postgres -h postgres