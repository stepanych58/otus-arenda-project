kubectl port-forward --namespace otus-infra  svc/kube-prometheus-stack-prometheus 9090

kubectl --namespace otus-infra get pod -l "app.kubernetes.io/name=grafana,app.kubernetes.io/instance=kube-prometheus-stack" -oname
kubectl --namespace otus-infra port-forward pod/kube-prometheus-stack-grafana-5bcbcf746f-lbbgb 3000
kubectl --namespace otus-infra get secrets kube-prometheus-stack-grafana -o jsonpath="{.data.admin-password}" | base64 -d 
