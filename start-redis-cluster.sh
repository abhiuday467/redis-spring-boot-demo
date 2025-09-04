#!/bin/bash

#!/bin/bash
set -euo pipefail

echo "Starting 6-node Redis Cluster + MySQL..."
docker compose -f docker-compose.redis-cluster-sql.yml up -d

echo "Waiting for Redis nodes to accept connections..."
for p in 7000 7001 7002 7003 7004 7005; do
  for i in {1..60}; do
    if docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-$p redis-cli -p $p ping >/dev/null 2>&1; then
      echo "redis-$p is up"
      break
    fi
    printf "."; sleep 2
    if [ $i -eq 60 ]; then
      echo "\nTimeout waiting for redis-$p to start" >&2
      exit 1
    fi
  done
done

echo "Bootstrapping the cluster (3 masters, 3 replicas)..."
docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-7000 \
  redis-cli --cluster create \
  redis-7000:7000 redis-7001:7001 redis-7002:7002 \
  redis-7003:7003 redis-7004:7004 redis-7005:7005 \
  --cluster-replicas 1 --cluster-yes

echo "Verifying cluster state..."
docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-7000 redis-cli -c -p 7000 cluster info | grep cluster_state || true
docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-7000 sh -lc "redis-cli -c -p 7000 cluster nodes | head"

echo "Cluster is set up. To stop:"
echo "  docker compose -f docker-compose.redis-cluster-sql.yml down"

echo "Cluster compose is ready."
echo "To run your Spring Boot app in cluster mode:"
echo "mvn spring-boot:run -Dspring.profiles.active=cluster"
echo ""
echo "To stop the cluster + MySQL:"
echo "docker compose -f docker-compose.redis-cluster-sql.yml down"
