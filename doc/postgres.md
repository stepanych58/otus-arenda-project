Чтобы установить postgres в minikube необходимо выпольнить:

1) подготовку миникуба
   minikube delete
   minikube start --cni=bridge
   minikube addons enable storage-provisioner
   minikube addons enable default-storageclass
2) выполнить команду установки миникуба в namespace infra
   helm install otus-pstgr oci://registry-1.docker.io/bitnamicharts/postgresql -n otus-infra --create-namespace -f
   ./helm-chart/infra/postgres/values.yaml

3) затем выполнить проброс порта на localhost
   kubectl port-forward --namespace otus-infra svc/otus-pstgr-postgresql-primary 5432:5432
4) password можно получить из секрета командой
   $POSTGRES_PASSWORD = (kubectl get secret otus-pstgr-postgresql -o jsonpath="{.data.postgres-password}" -n otus-infra | base64 -d)