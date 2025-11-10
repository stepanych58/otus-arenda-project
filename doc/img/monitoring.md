Устанавливаем графану, прометеус в одном чарте:
helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n otus-infra

kubectl --namespace otus-infra get pod -l "app.kubernetes.io/name=grafana,app.kubernetes.io/instance=kube-prometheus-stack" -oname

kube-prometheus-stack has been installed. Check its status by running:
kubectl --namespace otus-infra get pods -l "release=kube-prometheus-stack"

Получаем пароль для графаны
kubectl --namespace otus-infra get secrets kube-prometheus-stack-grafana -o jsonpath="{.data.admin-password}" | base64 -d ; echo

запускаем графану через port-forward
export
POD_NAME=$(kubectl --namespace otus-infra get pod -l "app.kubernetes.io/name=grafana,app.kubernetes.io/instance=kube-prometheus-stack" -oname)
kubectl --namespace otus-infra port-forward $POD_NAME 3000

делаем стрес тест нашему микросервису
для гет запроса ab -n 50000 -c 50 http://arch.homework/otus-user-service/user-service/api/users?page=0"&"size=4990

для пост запроса ab -n 500000 -c 50 -T "application/json" -p create_user.txt http://arch.homework/otus-user-service/user-service/api/users

create_user.txt:
{
"firstName": "string",
"lastName": "string",
"gender": true,
"birthDate": "2006-01-16T01:20:47.943Z",
"createdAt": "1978-02-14T08:51:15.829Z",
"modifiedAt": "1982-06-26T04:31:39.904Z"
}

установка эластика
helm repo add elastic https://helm.elastic.co

helm install elasticsearch --set replicas=1 --set persistence.enabled=false elastic/elasticsearch -n otus-infra

helm install kibana elastic/kibana -n otus-infra

helm upgrade kibana elastic/kibana -f values.yaml -n otus-infra

kubectl port-forward svc/kibana-kibana 5601:5601 -n otus-infra

helm repo add fluent https://fluent.github.io/helm-charts
helm upgrade --install fluent-bit fluent/fluent-bit -f fluentbit-values.yaml -n otus-infra

kubectl port-forward --namespace otus-msa svc/keycloack-keycloak ${SERVICE_PORT}:${SERVICE_PORT} & echo "http://127.0.0.1:${SERVICE_PORT}/"
