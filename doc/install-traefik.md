#### Добавляем репозиторий с хелм чартом

helm repo add traefik https://traefik.github.io/charts

#### Установка

helm install traefik traefik/traefik --namespace otus-msa -f ./helm-chart/infra/traefik/values.yaml --wait
helm upgrade --install traefik traefik/traefik --namespace otus-msa -f ./helm-chart/infra/traefik/values.yaml --wait
helm uninstall traefik -n otus-msa

#### Добавление CRD для конфигураций ingress-route  и middleware

kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.3.0/standard-install.yaml -n otus-msa
kubectl apply
-f https://raw.githubusercontent.com/traefik/traefik/v2.11/docs/content/reference/dynamic-configuration/kubernetes-crd-definition-v1.yml

#### Содание ingress-route и middleware

[ingress-route](/helm-chart/user-service-chart/templates/user-service-ingressroute.yaml)

[forward-auth-middleware](/helm-chart/user-service-chart/templates/forward-auth-middleware.yaml)
_для проброса заголовка Authorization_
[traefik-json-body2header-middleware](/helm-chart/user-service-chart/templates/traefik-json-body2header-middleware.yaml)
_для проброса sub из ответа keycloak в заголовок x-user-id, но чет не завелось, поэтому работаем полностью с токеном в самом сервисе_