helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install otus-kafka bitnami/kafka -n otus-infra --create-namespace

helm install kafka-ui kafka-ui/kafka-ui --set envs.config.KAFKA_CLUSTERS_0_NAME=local --set
envs.config.KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=otus-kafka:9092

kubectl port-forward svc/kafka-ui 8082:80 -n otus-infra
kubectl port-forward svc/otus-kafka 9092:9092 -n otus-infra

helm install my-kafka bitnami/kafka --namespace otus-infra --create-namespace --set kraft.enabled=true --set
listeners.clientProtocol=plaintext --set listeners.external.protocol=plaintext

helm install my-kafka bitnami/kafka -f ./helm-chart/infra/kafka/values.yaml -n otus-infra