docker save doc-man-render:latest | gzip > doc-man-render.tar.gz
docker save 481436215387.dkr.ecr.ap-east-1.amazonaws.com/docman-ui:9544e46de5cf66b1ecdc167da5cd2de951ec1a9d | gzip > docman-ui.tar.gz
docker save hasura/graphql-engine:v2.15.2 | gzip > graphql-engine.tar.gz
docker save postgres:12 | gzip > postgres.tar.gz